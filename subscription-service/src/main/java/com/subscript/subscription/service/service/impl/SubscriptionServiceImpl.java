package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.*;
import com.subscript.subscription.service.repository.*;
import com.subscript.subscription.service.service.interfaces.AuditLogService;
import com.subscript.subscription.service.service.interfaces.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final SubscriptionPlanRepository planRepository;
    private final InvoiceRepository invoiceRepository;
    private final UsageDataRepository usageDataRepository;
    private final PlanMeterMappingRepository planMeterMappingRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final AuditLogService auditLogService;

    // SUBSCRIBE — if plan has trial_days > 0 → trial; else → active + invoice
    @Override
    @Transactional
    public Subscription subscribe(Integer customerId, Integer planId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planId));

        subscriptionRepository
                .findByCustomer_CustomerIdAndPlan_PlanIdAndStatusIn(
                        customerId,
                        planId,
                        List.of(
                                Subscription.Status.active,
                                Subscription.Status.trial))
                .ifPresent(subscription -> {
                    throw new RuntimeException(
                            "Customer already has an active subscription for this plan.");
                });

        Subscription sub = new Subscription();
        sub.setCustomer(customer);
        sub.setService(plan.getService());
        sub.setPlan(plan);
        sub.setApiKey(UUID.randomUUID().toString().replace("-", "").substring(0, 32));
        sub.setStartDate(LocalDate.now());

        Integer trialDays = plan.getTrialDays();
        if (trialDays != null && trialDays > 0) {
            // TRIAL BRANCH — no invoice generated
            sub.setStatus(Subscription.Status.trial);
            sub.setTrialEndDate(LocalDate.now().plusDays(trialDays));
            sub = subscriptionRepository.save(sub);
            auditLogService.log(
                    customer.getUser().getUserId(),
                    "TRIAL_STARTED",
                    "SUBSCRIPTION",
                    sub.getSubscriptionId(),
                    "Trial started for plan: " + plan.getPlanName() + ", ends: " + sub.getTrialEndDate());
        } else {
            // ACTIVE BRANCH — generate first invoice immediately
            sub.setStatus(Subscription.Status.active);
            sub.setRenewalDate(LocalDate.now().plusMonths(1));
            sub = subscriptionRepository.save(sub);
            generateInvoice(sub, plan);
        }

        return sub;
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    @Override
    public List<Subscription> getSubscriptionsByCustomer(Integer customerId) {
        return subscriptionRepository.findByCustomer_CustomerId(customerId);
    }

    @Override
    public Subscription getSubscriptionById(Integer id) {
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));
    }

    @Override
    @Transactional
    public Subscription upgradePlan(Integer subscriptionId, Integer newPlanId) {
        Subscription sub = getSubscriptionById(subscriptionId);
        SubscriptionPlan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + newPlanId));
        sub.setPlan(newPlan);
        return subscriptionRepository.save(sub);
    }

    @Override
    @Transactional
    public Subscription cancelSubscription(Integer id) {
        Subscription sub = getSubscriptionById(id);
        sub.setStatus(Subscription.Status.cancelled);
        sub.setCancelledAt(LocalDateTime.now());
        return subscriptionRepository.save(sub);
    }

    @Override
    public void deleteSubscription(Integer id) {
        getSubscriptionById(id);
        subscriptionRepository.deleteById(id);
    }

    /**
     * Idempotent trial status check.
     * No-op if subscription is not in 'trial' status.
     * Checks time limit AND usage limit; converts on first match.
     * Wrapped in @Transactional to prevent double-conversion race.
     */
    @Override
    @Transactional
    public void checkTrialStatus(Integer subscriptionId) {
        // Re-fetch inside transaction with a fresh read to prevent race condition
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        // IDEMPOTENCY GUARD — already converted, nothing to do
        if (sub.getStatus() != Subscription.Status.trial) {
            return;
        }

        boolean timeLimitHit = sub.getTrialEndDate() != null
                && !LocalDate.now().isBefore(sub.getTrialEndDate());

        boolean usageLimitHit = false;
        List<PlanMeterMapping> mappings = planMeterMappingRepository
                .findByPlan_PlanId(sub.getPlan().getPlanId());
        List<UsageData> usageList = usageDataRepository
                .findBySubscription_SubscriptionId(subscriptionId);

        for (PlanMeterMapping mapping : mappings) {
            if (mapping.getMaxLimit() != null) {
                int totalUsage = usageList.stream()
                        .filter(u -> u.getMeter() != null
                                && u.getMeter().getMeterId().equals(mapping.getMeter().getMeterId())
                                && u.getApiCalls() != null)
                        .mapToInt(UsageData::getApiCalls)
                        .sum();
                if (totalUsage >= mapping.getMaxLimit()) {
                    usageLimitHit = true;
                    break;
                }
            }
        }

        if (!timeLimitHit && !usageLimitHit) {
            return; // Trial still valid — do nothing
        }

        // Convert trial
        List<PaymentMethod> paymentMethods = paymentMethodRepository
                .findByCustomer_CustomerId(sub.getCustomer().getCustomerId());

        if (!paymentMethods.isEmpty()) {
            sub.setStatus(Subscription.Status.active);
            sub.setRenewalDate(LocalDate.now().plusMonths(1));
            subscriptionRepository.save(sub);
            generateInvoice(sub, sub.getPlan());
            auditLogService.log(
                    sub.getCustomer().getUser().getUserId(),
                    "TRIAL_CONVERTED_ACTIVE",
                    "SUBSCRIPTION",
                    sub.getSubscriptionId(),
                    "Trial ended (" + (timeLimitHit ? "time limit" : "usage limit") + "). Converted to active.");
        } else {
            sub.setStatus(Subscription.Status.expired);
            subscriptionRepository.save(sub);
            auditLogService.log(
                    sub.getCustomer().getUser().getUserId(),
                    "TRIAL_EXPIRED",
                    "SUBSCRIPTION",
                    sub.getSubscriptionId(),
                    "Trial ended (" + (timeLimitHit ? "time limit" : "usage limit")
                            + "). Expired — no payment method.");
        }
    }

    /** Daily midnight job — checks every subscription still in trial */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processDailyTrials() {
        List<Subscription> trials = subscriptionRepository.findByStatus(Subscription.Status.trial);
        for (Subscription trial : trials) {
            try {
                checkTrialStatus(trial.getSubscriptionId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private void generateInvoice(Subscription sub, SubscriptionPlan plan) {
        Invoice invoice = new Invoice();
        invoice.setCustomer(sub.getCustomer());
        invoice.setSubscription(sub);
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setBaseAmount(plan.getBasePrice() != null ? plan.getBasePrice() : BigDecimal.ZERO);
        invoice.setDiscountAmount(BigDecimal.ZERO);
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setTotalAmount(plan.getBasePrice() != null ? plan.getBasePrice() : BigDecimal.ZERO);
        invoice.setPeriodStart(LocalDate.now());
        invoice.setPeriodEnd(LocalDate.now().plusMonths(1));
        invoice.setDueDate(LocalDate.now().plusDays(15));
        invoice.setStatus(Invoice.Status.pending);
        invoiceRepository.save(invoice);
    }
}
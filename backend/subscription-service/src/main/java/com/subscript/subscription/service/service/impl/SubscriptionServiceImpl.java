package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.*;
import com.subscript.subscription.service.repository.*;
import com.subscript.subscription.service.service.interfaces.AuditLogService;
import com.subscript.subscription.service.service.interfaces.DiscountService;
import com.subscript.subscription.service.service.interfaces.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.subscript.subscription.api.wrapper.mapper.SubscriptionMapper;
import com.subscript.subscription.api.wrapper.response.SubscriptionResponse;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
    private final DiscountService discountService;

    // SUBSCRIBE — if plan has trial_days > 0 → trial; else → active + invoice
    @Override
    @Transactional
    public SubscriptionResponse subscribe(
            Integer customerId,
            Integer planId,
            String couponCode) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        SubscriptionPlan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + planId));

        // Optional coupon: reuse the existing validation; invalid/expired => HTTP 400.
        Discount discount = null;
        if (couponCode != null && !couponCode.isBlank()) {
            try {
                discount = discountService.validateCoupon(couponCode.trim());
            } catch (RuntimeException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
            }
        }

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

        Subscription subscription = new Subscription();

        subscription.setCustomer(customer);
        subscription.setService(plan.getService());
        subscription.setPlan(plan);

        subscription.setApiKey(
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 32));

        subscription.setStartDate(LocalDate.now());

        BigDecimal basePrice = plan.getBasePrice() != null ? plan.getBasePrice() : BigDecimal.ZERO;
        BigDecimal finalAmount;

        Integer trialDays = plan.getTrialDays();

        if (trialDays != null && trialDays > 0) {

            subscription.setStatus(Subscription.Status.trial);

            subscription.setTrialEndDate(
                    LocalDate.now().plusDays(trialDays));

            subscription = subscriptionRepository.save(subscription);

            // Trial has no invoice yet; a coupon (if supplied) is validated above
            // but only applied when the trial converts and an invoice is created.
            finalAmount = basePrice;

            auditLogService.log(
                    customer.getUser().getUserId(),
                    "TRIAL_STARTED",
                    "SUBSCRIPTION",
                    subscription.getSubscriptionId(),
                    "Trial started for plan: "
                            + plan.getPlanName());

        } else {

            subscription.setStatus(Subscription.Status.active);

            subscription.setRenewalDate(
                    LocalDate.now().plusMonths(1));

            subscription = subscriptionRepository.save(subscription);

            finalAmount = generateInvoice(subscription, plan, discount);

            if (discount != null) {
                // Count the redemption so max_uses stays enforceable. The Discount
                // is managed within this @Transactional, so the update is flushed.
                discount.setCurrentUses(
                        (discount.getCurrentUses() == null ? 0 : discount.getCurrentUses()) + 1);
            }

            auditLogService.log(
                    customer.getUser().getUserId(),
                    "SUBSCRIPTION_CREATED",
                    "SUBSCRIPTION",
                    subscription.getSubscriptionId(),
                    "Subscription created successfully.");
        }

        SubscriptionResponse response = SubscriptionMapper.toResponse(subscription);
        response.setDiscountAmount(basePrice.subtract(finalAmount));
        response.setFinalAmount(finalAmount);
        return response;
    }

    @Override
    public List<SubscriptionResponse> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(SubscriptionMapper::toResponse)
                .toList();
    }

    @Override
    public List<SubscriptionResponse> getSubscriptionsByCustomer(Integer customerId) {
        return subscriptionRepository.findByCustomer_CustomerId(customerId).stream()
                .map(SubscriptionMapper::toResponse)
                .toList();
    }

    @Override
    public SubscriptionResponse getSubscriptionById(Integer id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));
        return SubscriptionMapper.toResponse(sub);
    }

    @Override
    @Transactional
    public SubscriptionResponse upgradePlan(Integer subscriptionId, Integer newPlanId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));
        SubscriptionPlan newPlan = planRepository.findById(newPlanId)
                .orElseThrow(() -> new RuntimeException("Plan not found: " + newPlanId));
        sub.setPlan(newPlan);
        Subscription saved = subscriptionRepository.save(sub);
        return SubscriptionMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SubscriptionResponse cancelSubscription(Integer id) {
        Subscription sub = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + id));
        sub.setStatus(Subscription.Status.cancelled);
        sub.setCancelledAt(LocalDateTime.now());
        Subscription saved = subscriptionRepository.save(sub);
        return SubscriptionMapper.toResponse(saved);
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
            generateInvoice(sub, sub.getPlan(), null);
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

    @Override
    public Map<String, Object> getTrialStatus(Integer subscriptionId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        long daysRemaining = 0;
        if (sub.getTrialEndDate() != null) {
                daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), sub.getTrialEndDate());
                if (daysRemaining < 0) daysRemaining = 0;
        }

        boolean isTrialActive = sub.getStatus() == Subscription.Status.trial;

        List<UsageData> usageList = usageDataRepository
                .findBySubscription_SubscriptionId(subscriptionId);
        List<PlanMeterMapping> mappings = planMeterMappingRepository
                .findByPlan_PlanId(sub.getPlan().getPlanId());

        List<Map<String, Object>> meterStats = mappings.stream().map(m -> {
                int consumed = usageList.stream()
                        .filter(u -> u.getMeter() != null
                                && u.getMeter().getMeterId().equals(m.getMeter().getMeterId())
                                && u.getApiCalls() != null)
                        .mapToInt(UsageData::getApiCalls)
                        .sum();
                Map<String, Object> stat = new java.util.LinkedHashMap<>();
                stat.put("meterId", m.getMeter().getMeterId());
                stat.put("meterName", m.getMeter().getName());
                stat.put("unit", m.getMeter().getUnit());
                stat.put("usageConsumed", consumed);
                stat.put("freeUnits", m.getFreeUnits());
                stat.put("maxLimit", m.getMaxLimit());
                return stat;
        }).toList();

        Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("subscriptionId", sub.getSubscriptionId());
        response.put("status", sub.getStatus());
        response.put("trialEndDate", sub.getTrialEndDate() != null ? sub.getTrialEndDate().toString() : null);
        response.put("daysRemaining", daysRemaining);
        response.put("isTrialActive", isTrialActive);
        response.put("meterUsage", meterStats);

        return response;
    }

    // ── private helpers ──────────────────────────────────────────────────────

    /**
     * Generates the pending invoice for a subscription. When a validated
     * {@code discount} is supplied it is applied to the existing invoice columns
     * (base / discount / tax / total); with no discount the amounts are identical
     * to the previous behaviour. Returns the final total charged.
     */
    private BigDecimal generateInvoice(Subscription sub, SubscriptionPlan plan, Discount discount) {
        BigDecimal base = plan.getBasePrice() != null ? plan.getBasePrice() : BigDecimal.ZERO;

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (discount != null) {
            if (discount.getDiscountType() == Discount.DiscountType.percentage) {
                discountAmount = base
                        .multiply(discount.getDiscountValue())
                        .divide(BigDecimal.valueOf(100));
            } else { // fixed_amount
                discountAmount = discount.getDiscountValue();
            }
            if (discountAmount.compareTo(base) > 0) {
                discountAmount = base; // never charge below zero
            }
        }

        BigDecimal total = base.subtract(discountAmount);

        Invoice invoice = new Invoice();
        invoice.setCustomer(sub.getCustomer());
        invoice.setSubscription(sub);
        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis());
        invoice.setBaseAmount(base);
        invoice.setDiscountAmount(discountAmount);
        invoice.setTaxAmount(BigDecimal.ZERO);
        invoice.setTotalAmount(total);
        invoice.setPeriodStart(LocalDate.now());
        invoice.setPeriodEnd(LocalDate.now().plusMonths(1));
        invoice.setDueDate(LocalDate.now().plusDays(15));
        invoice.setStatus(Invoice.Status.pending);
        invoiceRepository.save(invoice);

        return total;
    }
}
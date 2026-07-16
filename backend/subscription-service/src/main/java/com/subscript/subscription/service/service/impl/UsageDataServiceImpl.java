package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.*;
import com.subscript.subscription.api.wrapper.mapper.UsageDataMapper;
import com.subscript.subscription.api.wrapper.request.InvoiceGenerateRequest;
import com.subscript.subscription.api.wrapper.request.UsageDataRequest;
import com.subscript.subscription.api.wrapper.response.UsageDataResponse;
import com.subscript.subscription.api.wrapper.response.UsageSummaryResponse;
import com.subscript.subscription.service.repository.*;
import com.subscript.subscription.service.service.interfaces.InvoiceService;
import com.subscript.subscription.service.service.interfaces.SubscriptionService;
import com.subscript.subscription.service.service.interfaces.UsageDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsageDataServiceImpl implements UsageDataService {

    private final UsageDataRepository usageDataRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MeterRepository meterRepository;
    private final PlanMeterMappingRepository planMeterMappingRepository;

    @Lazy
    private final SubscriptionService subscriptionService;

    private final InvoiceService invoiceService;

    @Override
    @Transactional
    public UsageDataResponse recordUsage(UsageDataRequest request) {

        UsageData usage = recordUsage(
                request.getSubscriptionId(),
                request.getMeterId(),
                request.getApiCalls(),
                request.getActiveUsers(),
                request.getStorageGb());

        return UsageDataMapper.toResponse(usage);
    }

    @Transactional
    public UsageData recordUsage(
            Integer subscriptionId,
            Integer meterId,
            Integer apiCalls,
            Integer activeUsers,
            BigDecimal storageGb) {

        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        if (sub.getStatus() == Subscription.Status.expired
                || sub.getStatus() == Subscription.Status.cancelled) {

            throw new IllegalStateException(
                    "Cannot record usage for subscription status: "
                            + sub.getStatus());
        }

        Meter meter = null;

        if (meterId != null) {

            meter = meterRepository.findById(meterId)
                    .orElseThrow(() -> new RuntimeException("Meter not found: " + meterId));

            boolean mapped = planMeterMappingRepository
                    .findByPlan_PlanId(sub.getPlan().getPlanId())
                    .stream()
                    .anyMatch(mapping -> mapping.getMeter().getMeterId().equals(meterId));

            if (!mapped) {
                throw new IllegalArgumentException(
                        "Meter " + meterId +
                                " is not mapped with Plan " +
                                sub.getPlan().getPlanId());
            }
        }


        UsageData usage = new UsageData();

        usage.setSubscription(sub);
        usage.setSaasService(sub.getService());
        usage.setMeter(meter);

        usage.setPeriodStart(LocalDate.now().withDayOfMonth(1));
        usage.setPeriodEnd(
                LocalDate.now()
                        .withDayOfMonth(1)
                        .plusMonths(1)
                        .minusDays(1));

        usage.setApiCalls(apiCalls);
        usage.setActiveUsers(activeUsers);
        usage.setStorageGb(
                storageGb == null
                        ? BigDecimal.ZERO
                        : storageGb);

        usage.setOverageCharge(BigDecimal.ZERO);

        UsageData saved = usageDataRepository.save(usage);

        if (sub.getStatus() == Subscription.Status.trial) {
            subscriptionService.checkTrialStatus(subscriptionId);
        }

        UsageSummaryResponse summary = getUsageSummary(subscriptionId);

        // Usage-based billing: when this record exceeds the plan's included units,
        // bill the overage by REUSING the existing invoice generation (which adds
        // the latest usage's overageCharge to base + tax). No new billing logic.
        if (sub.getStatus() != Subscription.Status.trial &&
            summary.getOverage() != null && summary.getOverage().compareTo(BigDecimal.ZERO) > 0) {
            InvoiceGenerateRequest invoiceRequest = new InvoiceGenerateRequest();
            invoiceRequest.setSubscriptionId(subscriptionId);
            invoiceService.generateInvoice(invoiceRequest);
        }

        return saved;
    }

    @Override
    public List<UsageDataResponse> getUsageBySubscription(Integer subscriptionId) {

        return usageDataRepository
                .findBySubscription_SubscriptionId(subscriptionId)
                .stream()
                .map(UsageDataMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Meter-based usage summary — everything computed here (single source of
     * truth). Walks subscription → plan → plan_meter_mapping → meter and returns
     * the configured limit, remaining, percentage and overage. If the plan has
     * no meter mapping, hasMeter=false so the UI can show a message.
     */
    @Override
    public UsageSummaryResponse getUsageSummary(Integer subscriptionId) {

        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        long currentUsage = usageDataRepository
                .findBySubscription_SubscriptionId(subscriptionId)
                .stream()
                .mapToLong(u -> u.getApiCalls() == null ? 0L : u.getApiCalls())
                .sum();

        List<PlanMeterMapping> mappings = planMeterMappingRepository
                .findByPlan_PlanId(sub.getPlan().getPlanId());

        if (mappings.isEmpty()) {
            UsageSummaryResponse noMeter = new UsageSummaryResponse();
            noMeter.setSubscriptionId(subscriptionId);
            noMeter.setHasMeter(false);
            noMeter.setCurrentUsage(currentUsage);
            return noMeter;
        }

        PlanMeterMapping mapping = mappings.get(0);
        int freeUnits = mapping.getFreeUnits() == null ? 0 : mapping.getFreeUnits();
        Integer usageLimit = mapping.getMaxLimit();
        BigDecimal pricePerUnit = mapping.getPricePerUnit() == null
                ? BigDecimal.ZERO
                : mapping.getPricePerUnit();

        long remaining = 0L;
        double usagePercent = 0.0;
        if (usageLimit != null && usageLimit > 0) {
            remaining = Math.max(0L, usageLimit - currentUsage);
            usagePercent = (currentUsage * 100.0) / usageLimit;
        }

        BigDecimal overage = BigDecimal.ZERO;
        if (currentUsage > freeUnits) {
            overage = BigDecimal.valueOf(currentUsage - freeUnits).multiply(pricePerUnit);
        }

        UsageSummaryResponse summary = new UsageSummaryResponse();
        summary.setSubscriptionId(subscriptionId);
        summary.setHasMeter(true);
        summary.setMeterName(mapping.getMeter().getName());
        summary.setUnit(mapping.getMeter().getUnit());
        summary.setFreeUnits(freeUnits);
        summary.setUsageLimit(usageLimit);
        summary.setPricePerUnit(pricePerUnit);
        summary.setCurrentUsage(currentUsage);
        summary.setRemaining(remaining);
        summary.setUsagePercent(usagePercent);
        summary.setOverage(overage);
        return summary;
    }

    @Override
    public UsageDataResponse getLatestUsage(Integer subscriptionId) {

        UsageData usage = usageDataRepository
                .findTopBySubscription_SubscriptionIdOrderByRecordedAtDesc(subscriptionId)
                .orElseThrow(() -> new RuntimeException(
                        "No usage found for subscription: " + subscriptionId));

        return UsageDataMapper.toResponse(usage);
    }

    @Override
    public UsageDataResponse getUsageById(Integer id) {

        UsageData usage = usageDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usage not found: " + id));

        return UsageDataMapper.toResponse(usage);
    }

    @Override
    public void deleteUsage(Integer id) {
        usageDataRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteUsageBySubscription(Integer subscriptionId) {
        usageDataRepository.deleteBySubscription_SubscriptionId(subscriptionId);
    }
}
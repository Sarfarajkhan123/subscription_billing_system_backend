package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.*;
import com.subscript.subscription.api.wrapper.mapper.UsageDataMapper;
import com.subscript.subscription.api.wrapper.request.UsageDataRequest;
import com.subscript.subscription.api.wrapper.response.UsageDataResponse;
import com.subscript.subscription.service.repository.*;
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

        BigDecimal overageCharge = BigDecimal.ZERO;

        if (sub.getStatus() != Subscription.Status.trial) {

            Integer usageLimit = sub.getPlan().getUsageLimit();

            if (usageLimit != null
                    && apiCalls != null
                    && apiCalls > usageLimit) {

                int overage = apiCalls - usageLimit;
                overageCharge = BigDecimal.valueOf(overage * 0.05);
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

        usage.setOverageCharge(overageCharge);

        UsageData saved = usageDataRepository.save(usage);

        if (sub.getStatus() == Subscription.Status.trial) {
            subscriptionService.checkTrialStatus(subscriptionId);
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
}
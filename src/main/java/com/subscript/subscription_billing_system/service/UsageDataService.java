package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.entity.Subscription;
import com.subscript.subscription_billing_system.entity.SubscriptionPlan;
import com.subscript.subscription_billing_system.entity.UsageData;
import com.subscript.subscription_billing_system.repository.SubscriptionRepository;
import com.subscript.subscription_billing_system.repository.UsageDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsageDataService {

    private final UsageDataRepository usageDataRepository;
    private final SubscriptionRepository subscriptionRepository;

    // RECORD usage — called when the customer uses the service
    public UsageData recordUsage(Integer subscriptionId, Integer apiCalls,
            Integer activeUsers, BigDecimal storageGb) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found: " + subscriptionId));

        SubscriptionPlan plan = sub.getPlan();

        // Calculate overage — if usage exceeds plan limit, charge extra
        BigDecimal overageCharge = BigDecimal.ZERO;
        if (plan.getUsageLimit() != null && apiCalls > plan.getUsageLimit()) {
            int overage = apiCalls - plan.getUsageLimit();
            // Charge ₹0.05 per extra API call (demo pricing)
            overageCharge = BigDecimal.valueOf(overage * 0.05);
        }

        UsageData usage = new UsageData();
        usage.setSubscription(sub);
        usage.setPeriodStart(LocalDate.now().withDayOfMonth(1));
        usage.setPeriodEnd(LocalDate.now().withDayOfMonth(1).plusMonths(1).minusDays(1));
        usage.setApiCalls(apiCalls);
        usage.setActiveUsers(activeUsers);
        usage.setStorageGb(storageGb != null ? storageGb : BigDecimal.ZERO);
        usage.setOverageCharge(overageCharge);

        return usageDataRepository.save(usage);
    }

    // GET all usage for a subscription
    public List<UsageData> getUsageBySubscription(Integer subscriptionId) {
        return usageDataRepository.findBySubscription_SubscriptionId(subscriptionId);
    }

    // GET latest usage record for a subscription (for the dashboard)
    public UsageData getLatestUsage(Integer subscriptionId) {
        return usageDataRepository
                .findTopBySubscription_SubscriptionIdOrderByRecordedAtDesc(subscriptionId)
                .orElseThrow(() -> new RuntimeException("No usage data found for subscription: " + subscriptionId));
    }

    // GET one usage record by ID
    public UsageData getUsageById(Integer id) {
        return usageDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usage record not found: " + id));
    }

    // DELETE usage record
    public void deleteUsage(Integer id) {
        getUsageById(id);
        usageDataRepository.deleteById(id);
    }
}
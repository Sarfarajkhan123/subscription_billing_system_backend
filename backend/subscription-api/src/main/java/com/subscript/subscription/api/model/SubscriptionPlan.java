package com.subscript.subscription.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Integer planId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private SaasService service;

    @Column(name = "plan_name", nullable = false, length = 100)
    private String planName;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_tier", nullable = false)
    private PlanTier planTier;

    @Enumerated(EnumType.STRING)
    @Column(name = "pricing_type")
    private PricingType pricingType = PricingType.fixed;

    @Column(name = "base_price", precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle")
    private BillingCycle billingCycle = BillingCycle.monthly;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "trial_days")
    private Integer trialDays = 0;

    @Column(name = "features", columnDefinition = "TEXT")
    private String features;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum PlanTier {
        basic, pro, enterprise
    }

    public enum PricingType {
        fixed, usage_based
    }

    public enum BillingCycle {
        monthly, annual
    }
}
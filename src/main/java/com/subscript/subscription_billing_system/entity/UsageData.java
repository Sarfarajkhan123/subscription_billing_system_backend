package com.subscript.subscription_billing_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Integer usageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;

    @Column(name = "metric_name", length = 100)
    private String metricName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private SaasService saasService;

    @Column(name = "period_start", nullable = false)
    private java.time.LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private java.time.LocalDate periodEnd;

    @Column(name = "value", precision = 15, scale = 4)
    private BigDecimal value;

    @Column(name = "unit", length = 30)
    private String unit;

    @Column(name = "api_calls")
    private Integer apiCalls;

    @Column(name = "active_users")
    private Integer activeUsers;

    @Column(name = "storage_gb", precision = 10, scale = 2)
    private BigDecimal storageGb = BigDecimal.ZERO;

    @Column(name = "overage_charge", precision = 10, scale = 2)
    private BigDecimal overageCharge;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @PrePersist
    protected void onCreate() {
        this.recordedAt = LocalDateTime.now();
    }
}

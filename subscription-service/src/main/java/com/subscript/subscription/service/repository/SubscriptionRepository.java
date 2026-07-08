package com.subscript.subscription.service.repository;

import com.subscript.subscription.api.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    List<Subscription> findByCustomer_CustomerId(Integer customerId);

    List<Subscription> findByCustomer_CustomerIdAndStatus(Integer customerId, Subscription.Status status);

    List<Subscription> findByRenewalDateAndStatus(LocalDate renewalDate, Subscription.Status status);

    List<Subscription> findByStatus(Subscription.Status status);

    Optional<Subscription> findByCustomer_CustomerIdAndPlan_PlanIdAndStatusIn(
            Integer customerId,
            Integer planId,
            List<Subscription.Status> statuses);
}

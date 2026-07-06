package com.subscript.subscription_billing_system.repository;

import com.subscript.subscription_billing_system.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    Optional<Discount> findByCouponCode(String couponCode);
    Optional<Discount> findByCouponCodeAndIsActiveTrue(String couponCode);
}

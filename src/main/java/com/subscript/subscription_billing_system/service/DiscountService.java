package com.subscript.subscription_billing_system.service;

import com.subscript.subscription_billing_system.entity.Discount;
import com.subscript.subscription_billing_system.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;

    public Discount createDiscount(Discount discount) {
        if (discountRepository.findByCouponCode(discount.getCouponCode()).isPresent()) {
            throw new RuntimeException("Coupon code already exists: " + discount.getCouponCode());
        }
        return discountRepository.save(discount);
    }

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public Discount getDiscountById(Integer id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found: " + id));
    }

    // Validate coupon — called at checkout
    public Discount validateCoupon(String couponCode) {
        Discount discount = discountRepository.findByCouponCodeAndIsActiveTrue(couponCode)
                .orElseThrow(() -> new RuntimeException("Invalid or expired coupon: " + couponCode));

        LocalDate today = LocalDate.now();
        if (today.isBefore(discount.getStartDate()) || today.isAfter(discount.getEndDate())) {
            throw new RuntimeException("Coupon is not valid today");
        }
        if (discount.getMaxUses() != null && discount.getCurrentUses() >= discount.getMaxUses()) {
            throw new RuntimeException("Coupon usage limit reached");
        }
        return discount;
    }

    public Discount updateDiscount(Integer id, Discount updated) {
        Discount existing = getDiscountById(id);
        if (updated.getDiscountValue() != null)
            existing.setDiscountValue(updated.getDiscountValue());
        if (updated.getEndDate() != null)
            existing.setEndDate(updated.getEndDate());
        if (updated.getMaxUses() != null)
            existing.setMaxUses(updated.getMaxUses());
        if (updated.getIsActive() != null)
            existing.setIsActive(updated.getIsActive());
        return discountRepository.save(existing);
    }

    public Discount deactivateDiscount(Integer id) {
        Discount discount = getDiscountById(id);
        discount.setIsActive(false);
        return discountRepository.save(discount);
    }

    public void deleteDiscount(Integer id) {
        getDiscountById(id);
        discountRepository.deleteById(id);
    }
}
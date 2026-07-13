package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.Discount;
import com.subscript.subscription.service.repository.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

public interface DiscountService {
    Discount createDiscount(Discount discount);
    List<Discount> getAllDiscounts();
    Discount getDiscountById(Integer id);
    Discount validateCoupon(String couponCode);
    Discount updateDiscount(Integer id, Discount updated);
    Discount deactivateDiscount(Integer id);
    void deleteDiscount(Integer id);
}

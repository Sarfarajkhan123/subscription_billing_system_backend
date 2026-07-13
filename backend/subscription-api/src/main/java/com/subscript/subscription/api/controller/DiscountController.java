package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.Discount;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DiscountController {

    ResponseEntity<Discount> createDiscount(Discount discount);

    ResponseEntity<List<Discount>> getAllDiscounts();

    ResponseEntity<Discount> getDiscountById(Integer id);

    ResponseEntity<Discount> validateCoupon(String code);

    ResponseEntity<Discount> updateDiscount(Integer id, Discount updated);

    ResponseEntity<Discount> deactivateDiscount(Integer id);

    ResponseEntity<String> deleteDiscount(Integer id);

}
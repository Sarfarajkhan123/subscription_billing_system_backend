package com.subscript.subscription_billing_system.controller;

import com.subscript.subscription_billing_system.entity.Discount;
import com.subscript.subscription_billing_system.service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DiscountController {

    private final DiscountService discountService;

    // POST /api/discounts
    @PostMapping
    public ResponseEntity<Discount> createDiscount(@RequestBody Discount discount) {
        return ResponseEntity.ok(discountService.createDiscount(discount));
    }

    // GET /api/discounts
    @GetMapping
    public ResponseEntity<List<Discount>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    // GET /api/discounts/1
    @GetMapping("/{id}")
    public ResponseEntity<Discount> getDiscountById(@PathVariable Integer id) {
        return ResponseEntity.ok(discountService.getDiscountById(id));
    }

    // GET /api/discounts/validate/SAVE20
    @GetMapping("/validate/{code}")
    public ResponseEntity<Discount> validateCoupon(@PathVariable String code) {
        return ResponseEntity.ok(discountService.validateCoupon(code));
    }

    // PUT /api/discounts/1
    @PutMapping("/{id}")
    public ResponseEntity<Discount> updateDiscount(
            @PathVariable Integer id,
            @RequestBody Discount updated) {
        return ResponseEntity.ok(discountService.updateDiscount(id, updated));
    }

    // PUT /api/discounts/1/deactivate
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Discount> deactivateDiscount(@PathVariable Integer id) {
        return ResponseEntity.ok(discountService.deactivateDiscount(id));
    }

    // DELETE /api/discounts/1
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDiscount(@PathVariable Integer id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok("Discount deleted");
    }
}
package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.DiscountController;
import com.subscript.subscription.api.model.Discount;
import com.subscript.subscription.service.service.interfaces.DiscountService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DiscountControllerImpl implements DiscountController {

    private final DiscountService discountService;

    // POST /api/discounts
    @Override
    @PostMapping
    public ResponseEntity<Discount> createDiscount(
            @RequestBody Discount discount) {

        return ResponseEntity.ok(discountService.createDiscount(discount));
    }

    // GET /api/discounts
    @Override
    @GetMapping
    public ResponseEntity<List<Discount>> getAllDiscounts() {

        return ResponseEntity.ok(discountService.getAllDiscounts());
    }

    // GET /api/discounts/{id}
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Discount> getDiscountById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(discountService.getDiscountById(id));
    }

    // GET /api/discounts/validate/{code}
    @Override
    @GetMapping("/validate/{code}")
    public ResponseEntity<Discount> validateCoupon(
            @PathVariable String code) {

        return ResponseEntity.ok(discountService.validateCoupon(code));
    }

    // PUT /api/discounts/{id}
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<Discount> updateDiscount(
            @PathVariable Integer id,
            @RequestBody Discount updated) {

        return ResponseEntity.ok(discountService.updateDiscount(id, updated));
    }

    // PUT /api/discounts/{id}/deactivate
    @Override
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Discount> deactivateDiscount(
            @PathVariable Integer id) {

        return ResponseEntity.ok(discountService.deactivateDiscount(id));
    }

    // DELETE /api/discounts/{id}
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDiscount(
            @PathVariable Integer id) {

        discountService.deleteDiscount(id);

        return ResponseEntity.ok("Discount deleted");
    }
}
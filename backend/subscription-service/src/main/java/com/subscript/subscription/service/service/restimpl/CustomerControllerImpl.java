package com.subscript.subscription.service.service.restimpl;

import com.subscript.subscription.api.controller.CustomerController;
import com.subscript.subscription.api.wrapper.request.CustomerRequest;
import com.subscript.subscription.api.wrapper.response.CustomerResponse;
import com.subscript.subscription.service.service.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerControllerImpl implements CustomerController {

    private final CustomerService customerService;

    @Override
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {

        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {

        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable Integer id) {

        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @Override
    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerResponse> getCustomerByEmail(
            @PathVariable String email) {

        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(
            @RequestParam String keyword) {

        return ResponseEntity.ok(customerService.searchByCompanyName(keyword));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable Integer id,
            @RequestBody CustomerRequest request) {

        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(
            @PathVariable Integer id) {

        customerService.deleteCustomer(id);

        return ResponseEntity.ok("Customer deleted");
    }
    // =========================================
    // Customer Self-Service
    // =========================================

    // GET /api/customers/me
    @Override
    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> getMyProfile(
            Principal principal) {

        return ResponseEntity.ok(
                customerService.getMyProfile(
                        principal.getName()));
    }

    // PUT /api/customers/me
    @Override
    @PutMapping("/me")
    public ResponseEntity<CustomerResponse> updateMyProfile(
            Principal principal,
            @RequestBody CustomerRequest request) {

        return ResponseEntity.ok(
                customerService.updateMyProfile(
                        principal.getName(),
                        request));
    }
}
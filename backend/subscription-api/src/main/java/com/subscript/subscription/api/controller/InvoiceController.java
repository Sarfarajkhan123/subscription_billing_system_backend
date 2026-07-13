package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.wrapper.request.InvoiceGenerateRequest;
import com.subscript.subscription.api.wrapper.response.InvoiceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface InvoiceController {

    @org.springframework.web.bind.annotation.PostMapping("/generate")
    ResponseEntity<InvoiceResponse> generateInvoice(
            @RequestBody InvoiceGenerateRequest request);

    ResponseEntity<List<InvoiceResponse>> getAllInvoices();

    ResponseEntity<List<InvoiceResponse>> getByCustomer(
            Integer customerId);

    ResponseEntity<List<InvoiceResponse>> getByStatus(
            String status);

    ResponseEntity<InvoiceResponse> getById(
            Integer id);

    ResponseEntity<InvoiceResponse> markAsPaid(
            Integer id);

    ResponseEntity<InvoiceResponse> markAsOverdue(
            Integer id);

    ResponseEntity<String> deleteInvoice(
            Integer id);
}
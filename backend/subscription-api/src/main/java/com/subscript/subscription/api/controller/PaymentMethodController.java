package com.subscript.subscription.api.controller;

import com.subscript.subscription.api.model.PaymentMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PaymentMethodController {

        ResponseEntity<PaymentMethod> addMethod(
                        Integer customerId,
                        PaymentMethod method);

        ResponseEntity<List<PaymentMethod>> getByCustomer(
                        Integer customerId);

        ResponseEntity<PaymentMethod> getById(
                        Integer id);

        ResponseEntity<PaymentMethod> setDefault(
                        Integer customerId,
                        Integer methodId);

        ResponseEntity<String> deleteMethod(
                        Integer id);

}
package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.*;
import com.subscript.subscription.service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
    Payment processPayment(Integer invoiceId, Integer customerId, String paymentMethod);
    List<Payment> getAllPayments();
    List<Payment> getPaymentsByCustomer(Integer customerId);
    List<Payment> getPaymentsByInvoice(Integer invoiceId);
    Payment getPaymentById(Integer id);
    void deletePayment(Integer id);
}

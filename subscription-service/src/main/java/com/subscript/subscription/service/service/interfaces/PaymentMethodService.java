package com.subscript.subscription.service.service.interfaces;

import com.subscript.subscription.api.model.Customer;
import com.subscript.subscription.api.model.PaymentMethod;
import com.subscript.subscription.service.repository.CustomerRepository;
import com.subscript.subscription.service.repository.PaymentMethodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

public interface PaymentMethodService {
    PaymentMethod addPaymentMethod(Integer customerId, PaymentMethod method);
    List<PaymentMethod> getMethodsByCustomer(Integer customerId);
    PaymentMethod getMethodById(Integer id);
    PaymentMethod setDefault(Integer customerId, Integer methodId);
    void deleteMethod(Integer id);
}

package com.subscript.subscription.service.service.impl;

import com.subscript.subscription.api.model.Invoice;
import com.subscript.subscription.api.model.Payment;
import com.subscript.subscription.api.model.Subscription;
import com.subscript.subscription.api.model.SubscriptionPlan;
import com.subscript.subscription.api.wrapper.response.DashboardResponse;
import com.subscript.subscription.service.repository.CustomerRepository;
import com.subscript.subscription.service.repository.InvoiceRepository;
import com.subscript.subscription.service.repository.PaymentRepository;
import com.subscript.subscription.service.repository.SubscriptionRepository;
import com.subscript.subscription.service.service.interfaces.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

/**
 * Computes the dashboard overview metrics on demand from the existing
 * repositories. No aggregate is stored; the backend stays the single source of
 * truth. Read-only — no data is mutated.
 */
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final SubscriptionRepository subscriptionRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public DashboardResponse getOverview() {

        List<Subscription> activeSubs =
                subscriptionRepository.findByStatus(Subscription.Status.active);

        BigDecimal mrr = activeSubs.stream()
                .map(this::monthlyPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal arr = mrr.multiply(BigDecimal.valueOf(12));

        long totalCustomers = customerRepository.count();

        long paidInvoices = invoiceRepository.countByStatus(Invoice.Status.paid);
        long pendingInvoices = invoiceRepository.countByStatus(Invoice.Status.pending);

        BigDecimal revenue = invoiceRepository.findByStatus(Invoice.Status.paid).stream()
                .map(Invoice::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal payments = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == Payment.Status.success)
                .map(Payment::getAmountPaid)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardResponse(
                mrr,
                arr,
                activeSubs.size(),
                totalCustomers,
                revenue,
                paidInvoices,
                pendingInvoices,
                payments);
    }

    /** Plan price normalised to a monthly figure (annual plans divided by 12). */
    private BigDecimal monthlyPrice(Subscription sub) {
        SubscriptionPlan plan = sub.getPlan();
        if (plan == null || plan.getBasePrice() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal base = plan.getBasePrice();
        return plan.getBillingCycle() == SubscriptionPlan.BillingCycle.annual
                ? base.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP)
                : base;
    }
}

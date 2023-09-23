package com.fashion.service.paypal;

import com.fashion.annotation.Order;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.Setter;
import lombok.SneakyThrows;

@Order(Integer.MAX_VALUE)
@Setter
public class PaypalService {

    private APIContext apiContext;

    @SneakyThrows
    public Payment getPaymentDetails(String paymentId) {
        return Payment.get(apiContext, paymentId);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);
        return payment.execute(apiContext, paymentExecution);
    }

    @SneakyThrows({PayPalRESTException.class})
    public Refund refund(String paymentId) {
        Payment payment = this.getPaymentDetails(paymentId);
        Transaction transaction = payment.getTransactions().get(0);
        Sale sale = transaction.getRelatedResources()
                .get(0)
                .getSale();
        Amount amount = sale.getAmount();
        amount.setDetails(null);
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setAmount(amount);
        DetailedRefund refund = sale.refund(apiContext, refundRequest);
        if (!"completed".equals(refund.getState())) {
            throw new IllegalArgumentException("refund error: " + paymentId);
        }
        return refund;
    }
}

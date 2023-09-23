package com.fashion.factory;

import com.fashion.constant.MethodPayment;
import com.fashion.dto.base.Result;
import com.fashion.dto.order.CreateOrderRequest;
import com.fashion.service.payment.IPaymentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentFactory {

    @NonNull
    private final IPaymentService codPaymentService;

    @NonNull
    private final IPaymentService paypalPaymentService;

    private IPaymentService defaultPaymentService = new DefaultPaymentService();

    @NonNull
    public IPaymentService getInstance(MethodPayment method) {
        assert method != null;
        switch (method) {
            case PAYPAL:
                return this.paypalPaymentService;
            case COD:
                return this.codPaymentService;
            default:
                return defaultPaymentService;
        }
    }

    public class DefaultPaymentService implements IPaymentService {

        @Override
        public Result<CreateOrderRequest> payment(CreateOrderRequest createOrderRequest) {
            throw new UnsupportedOperationException("not support this method payment");
        }
    }

}

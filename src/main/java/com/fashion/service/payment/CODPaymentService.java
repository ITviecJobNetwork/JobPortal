package com.fashion.service.payment;

import com.fashion.dto.base.Result;
import com.fashion.dto.order.CreateOrderRequest;
import lombok.Setter;

@Setter
public class CODPaymentService extends AbstractPaymentService {

    @Override
    public Result<CreateOrderRequest> payment(CreateOrderRequest createOrderRequest) {
        return this.tryCatchWithTransaction(session -> {
            this.createOrder(createOrderRequest, session);
            return Result.<CreateOrderRequest>builder()
                    .isSuccess(true)
                    .data(createOrderRequest)
                    .message("Đặt hàng thành công")
                    .successUrl("redirect:/order")
                    .build();
        }, createOrderRequest);
    }
}

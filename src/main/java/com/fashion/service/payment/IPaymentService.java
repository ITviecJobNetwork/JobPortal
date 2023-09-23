package com.fashion.service.payment;

import com.fashion.dto.base.Result;
import com.fashion.dto.order.CreateOrderRequest;

public interface IPaymentService {
    Result<CreateOrderRequest> payment(CreateOrderRequest createOrderRequest);
}

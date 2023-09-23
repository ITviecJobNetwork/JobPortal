package com.fashion.dto.order;

import com.fashion.constant.OrderStatus;
import lombok.Data;

@Data
public class OrderSearchRequest {
    private String code;
    private String createdBy;
    private String methodPayment;
    private String fromDate;
    private String toDate;
    private String phone;
    private String address;
    private OrderStatus status;
}
package com.fashion.dto.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderDetailResponse {
    private Long id;
    private String productName;
    private String productCode;
    private String productImg;
    private BigDecimal cost;
    private BigDecimal price;
    private Integer discount;
    private Integer quantity;
    private String orderCode;
    private Long productDetailId;
    private String size;
    private String color;
}
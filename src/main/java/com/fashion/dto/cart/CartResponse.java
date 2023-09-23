package com.fashion.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long cartId;
    private String productCode;
    private String productName;
    private String size;
    private String color;
    private BigDecimal price;
    private Integer discount;
    private Integer quantity;
    private String productDetailImage;
    private Integer maxQuantity;
}

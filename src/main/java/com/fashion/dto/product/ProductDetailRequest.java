package com.fashion.dto.product;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;

@Data
@Builder
@Getter
public class ProductDetailRequest {
    private Long id;
    private ColorProductDTO color;
    private String size;
    private Integer quantity;
    private BigDecimal cost;
    private BigDecimal price;
    private Integer discount;
    private Boolean active;
}

package com.fashion.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSearchRequest {
    private String codeName;
    private Long categoryId;
    private String fromDate;
    private String toDate;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String size;
}

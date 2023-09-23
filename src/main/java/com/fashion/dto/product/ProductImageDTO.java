package com.fashion.dto.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductImageDTO {
    private long productId;
    private String productName;
    private String productCode;
    private String imageUrl;
}

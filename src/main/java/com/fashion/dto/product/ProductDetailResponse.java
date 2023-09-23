package com.fashion.dto.product;

import com.fashion.dto.comment.CommentDTO;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Data
@SuperBuilder
public class ProductDetailResponse extends ProductResponse {
    private List<ProductResponse> relatedProducts;
    private Set<String> sizesSet;
    private List<CommentDTO> comments;
}

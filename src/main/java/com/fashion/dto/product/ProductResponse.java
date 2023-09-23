package com.fashion.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ProductResponse {
    private Long id;
    private String code;
    private String name;
    private String image;
    private String categoryName;
    private String shortDescription;
    private String description;
    private Long categoryId;
    private Boolean active;
    private Boolean isShowHome;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    @JsonIgnore
    private String colors;

    @JsonIgnore
    private String sizes;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createdDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date updatedDate;

    @JsonIgnore
    private Map<ColorProductDTO, List<ProductDetailRequest>> details;

    public Map<String, List<ProductDetailRequest>> getProductDetails() {
        if (Objects.isNull(this.details)) return new HashMap<>();
        Map<String, List<ProductDetailRequest>> productDetails = new LinkedHashMap<>();
        details.forEach((key, value) -> productDetails.put(key.getColor(), value));
        return productDetails;
    }

    public void setColorsAndSize() {
        if (Objects.isNull(details)) return;
        String colors = details.keySet().stream()
                .map(ColorProductDTO::getColor)
                .collect(Collectors.joining(","));
        if (CollectionUtils.isNotEmpty(details.values())) {
            String sizes = details.keySet()
                    .stream()
                    .map(details::get)
                    .findFirst()
                    .stream()
                    .flatMap(Collection::stream)
                    .map(ProductDetailRequest::getSize)
                    .collect(Collectors.joining(","));
            this.sizes = sizes;
        }
        this.colors = colors;
    }
}

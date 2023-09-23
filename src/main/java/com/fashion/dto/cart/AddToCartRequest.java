package com.fashion.dto.cart;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class AddToCartRequest {

    @NotNull(message = "Mã sản phẩm không được trống")
    private Long productDetailId;

    @NotNull(message = "Số lượng sản phẩm không được trống")
    @Min(value = 1, message = "Số lượng sản phẩm tối thiểu là 1")
    private Integer quantity;

}
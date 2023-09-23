package com.fashion.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImageType {
    PRODUCT("product/"),
    PRODUCT_COLOR("product-color/");

    private final String type;
}

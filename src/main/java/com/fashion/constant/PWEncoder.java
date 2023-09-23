package com.fashion.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum PWEncoder {
    BASE64("{{base64}}"),
    NOOP("{{noop}}");

    private final String type;

    public static PWEncoder fromType(String type) {
        return Arrays.stream(values())
                .filter(x -> x.type.equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow();
    }
}

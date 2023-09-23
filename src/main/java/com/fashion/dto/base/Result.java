package com.fashion.dto.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private boolean isSuccess;
    private String message;
    private T data;
    private String successUrl;
    private String failUrl;
}

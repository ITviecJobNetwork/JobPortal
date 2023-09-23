package com.fashion.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private boolean isRedirectPreviousUrl;
    private String redirectUrl;

    public BusinessException(String message, String redirectUrl) {
        super(message);
        this.redirectUrl = redirectUrl;
    }

    public BusinessException(String message, boolean isRedirectPreviousUrl) {
        super(message);
        this.isRedirectPreviousUrl = isRedirectPreviousUrl;
    }
}

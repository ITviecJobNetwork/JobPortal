package com.fashion.dto.exchangerate;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExchangeRateResponse {
    private boolean success;
    private String timestamp;
    private String base;
    private String date;
    private Map<String, BigDecimal> rates;
}

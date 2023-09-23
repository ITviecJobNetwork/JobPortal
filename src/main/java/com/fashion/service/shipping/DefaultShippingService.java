package com.fashion.service.shipping;

import com.fashion.constant.AppConstant;

import java.math.BigDecimal;

public class DefaultShippingService implements IShippingService {

    @Override
    public BigDecimal calculateFeeShipping(Object payload) {
        return AppConstant.ZERO;
    }
}

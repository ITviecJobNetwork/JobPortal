package com.fashion.service.tax;

import com.fashion.constant.AppConstant;

import java.math.BigDecimal;

public class DefaultTaxService implements ITaxService {


    @Override
    public BigDecimal calculateFeeTax(Object obj) {
        return AppConstant.ZERO;
    }

}

package com.fashion.service.tax;

import java.math.BigDecimal;

public interface ITaxService {

    BigDecimal calculateFeeTax(Object obj);
}

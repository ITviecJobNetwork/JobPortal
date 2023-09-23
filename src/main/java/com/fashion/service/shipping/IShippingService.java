package com.fashion.service.shipping;

import java.math.BigDecimal;

public interface IShippingService {
    BigDecimal calculateFeeShipping(Object payload);
}

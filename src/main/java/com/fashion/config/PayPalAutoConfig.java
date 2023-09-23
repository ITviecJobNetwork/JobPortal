package com.fashion.config;

import com.paypal.base.rest.APIContext;

import java.util.Map;
import java.util.Properties;

public class PayPalAutoConfig implements AutoConfig {

    @Override
    public void init(Map<String, Object> beanContainer, Properties config) {
        String clientId = config.getProperty("paypal.client-id");
        String clientSecret = config.getProperty("paypal.client-secret");
        String mode = config.getProperty("paypal.mode");
        APIContext apiContext = new APIContext(clientId, clientSecret, mode);
        beanContainer.put(APIContext.class.getName(), apiContext);
    }
}

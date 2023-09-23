package com.fashion.config;

import java.util.Map;
import java.util.Properties;

public interface AutoConfig {
    void init(Map<String, Object> beanContainer, Properties config);
}

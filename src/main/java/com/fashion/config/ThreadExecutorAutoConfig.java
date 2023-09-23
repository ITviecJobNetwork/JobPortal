package com.fashion.config;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ThreadExecutorAutoConfig implements AutoConfig {

    @Override
    public void init(Map<String, Object> beanContainer, Properties config) {
        String poolSize = config.getProperty("thread.pool-size", "5");
        Executor executor = Executors.newFixedThreadPool(NumberUtils.toInt(poolSize, 0));
        beanContainer.put(Executor.class.getName(), executor);
    }
}

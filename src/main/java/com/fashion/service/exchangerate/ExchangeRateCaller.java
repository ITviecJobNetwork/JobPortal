package com.fashion.service.exchangerate;

import com.fashion.annotation.Order;
import com.fashion.dto.exchangerate.ExchangeRateResponse;
import com.fashion.utils.ObjectUtil;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

@Order(Integer.MAX_VALUE)
@Setter
@Slf4j
public class ExchangeRateCaller {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofMinutes(1))
            .build();

    private Properties properties;

    @SneakyThrows({IOException.class, InterruptedException.class})
    public ExchangeRateResponse getRate() {
        long start = System.currentTimeMillis();
        log.info("call api get rate to exchange-rate");
        String exchangerateUrl = this.properties.getProperty("exchangerate.url");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(exchangerateUrl))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        log.info("call api exchange-rate done - duration: {} - response: {}", System.currentTimeMillis() - start, response.body());
        return ObjectUtil.jsonToObj(response.body(), ExchangeRateResponse.class);
    }
}

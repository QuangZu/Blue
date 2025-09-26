package com.techtack.blue.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class APIStockConfig {

    @Value("${stock.api.key}")
    private String apiKey;

    @Value("${stock.api.base}")
    private String baseUrl;

    public String getApiKey() {
        return apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
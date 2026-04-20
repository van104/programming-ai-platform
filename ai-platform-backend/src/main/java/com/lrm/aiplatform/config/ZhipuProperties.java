package com.lrm.aiplatform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "zhipu")
public class ZhipuProperties {
    private Api api = new Api();

    @Data
    public static class Api {
        private String key;
        private String url;
        private String model;
    }
}

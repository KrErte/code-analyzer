package com.codeanalyzer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConfigurationProperties(prefix = "anthropic.api")
@Data
public class ClaudeConfig {

    private String key;
    private String url;
    private String model;
    private int maxTokens;

    @Bean
    public WebClient claudeWebClient() {
        return WebClient.builder()
                .baseUrl(url)
                .defaultHeader("x-api-key", key)
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("content-type", "application/json")
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024))
                .build();
    }
}

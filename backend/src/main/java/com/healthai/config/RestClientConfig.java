package com.healthai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Value("${hospital.api.connect-timeout.seconds:5}")
    private int connectTimeoutSeconds;

    @Value("${hospital.api.read-timeout.seconds:20}")
    private int readTimeoutSeconds;

    @Bean
    public RestClient.Builder restClientBuilder() {

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(
                        Duration.ofSeconds(connectTimeoutSeconds)
                )
                .build();

        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(httpClient);

        requestFactory.setReadTimeout(
                Duration.ofSeconds(readTimeoutSeconds)
        );

        return RestClient.builder()
                .requestFactory(requestFactory);
    }
}
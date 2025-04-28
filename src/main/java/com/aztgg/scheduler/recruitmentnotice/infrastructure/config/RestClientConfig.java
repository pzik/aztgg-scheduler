package com.aztgg.scheduler.recruitmentnotice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean(name = "kakaoCareersPublicRestClient")
    public RestClient kakaoCareersPublicRestClient() {
        return RestClient.builder()
                .baseUrl("https://careers.kakao.com/public/api")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "woowahanCareersPublicRestClient")
    public RestClient woowahanCareersPublicRestClient() {
        return RestClient.builder()
                .baseUrl("https://career.woowahan.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "tossCareersPublicRestClient")
    public RestClient tossCareersPublicRestClient() {
        return RestClient.builder()
                .baseUrl("https://api-public.toss.im/api")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
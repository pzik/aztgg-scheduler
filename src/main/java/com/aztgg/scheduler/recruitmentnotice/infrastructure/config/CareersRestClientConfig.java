package com.aztgg.scheduler.recruitmentnotice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class CareersRestClientConfig {

    @Bean(name = "baseRestClient")
    public RestClient baseRestClient() {
        return RestClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

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

    @Bean(name = "lineCareersPublicRestClient")
    public RestClient lineCareersPublicRestClient() {
        return RestClient.builder()
                .baseUrl("https://careers.linecorp.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "daangnCareersPublicRestClient")
    public RestClient daangnCareersPublicRestClient() {
        return RestClient.builder()
                .baseUrl("https://about.daangn.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "kakaoBankCareersPublicRestClient")
    public RestClient kakaoBankCareersPublicRestClient() {
        return RestClient.builder()
                .baseUrl("https://recruit.kakaobank.com/api")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "naverCareersPublicRestClient")
    public RestClient naverCareersPublicRestClient() {
        return RestClient.builder()
                .baseUrl("https://recruit.navercorp.com/")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "molocoCareersPublicRestClient")
    public RestClient molocoCareersPublicRestClient() {
        return RestClient.builder()
                .baseUrl("https://boards-api.greenhouse.io/v1/boards/moloco")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "sendbirdCareersPublicRestClient")
    public RestClient sendbirdCareersPublicRestClient() {
        return RestClient.builder()
                .baseUrl("https://boards-api.greenhouse.io/v1/boards/sendbird")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
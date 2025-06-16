package com.aztgg.scheduler.recruitmentnotice.infrastructure.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfig {

    @PostConstruct
    public void init() {
        WebDriverManager.chromedriver().setup();
    }
}

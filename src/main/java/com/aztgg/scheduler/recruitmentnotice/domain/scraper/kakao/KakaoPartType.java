package com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KakaoPartType {

    TECHNOLOGY("TECHNOLOGY"),
    DESIGN("DESIGN"),
    ;

    private final String code;
}

package com.aztgg.scheduler.recruitmentnotice.domain.scraper;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KakaoPartType {

    TECHNOLOGY("TECHNOLOGY"),
    DESIGN_BRAND_MARKETING("DESIGN/BRAND_MARKETING"),
    ;

    private final String code;
}

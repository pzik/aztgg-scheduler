package com.aztgg.scheduler.company.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PredefinedCompany {

    NAVER("네이버", "네이버"),
    KAKAO("카카오", "카카오"),
    WOOWAHAN("배달의민족", "배달의민족"),
    ;

    private final String korean;
    private final String description;
}

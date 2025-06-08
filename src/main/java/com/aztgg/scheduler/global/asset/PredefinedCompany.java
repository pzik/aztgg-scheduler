package com.aztgg.scheduler.global.asset;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 계열사를 묶는 최고 모회사를 의미
 */
@Getter
@AllArgsConstructor
public enum PredefinedCompany {

    UNKNOWN("알수없음"),
    NAVER("네이버"),
    KAKAO("카카오"),
    LINE("라인"),
    COUPANG("쿠팡"),
    WOOWAHAN("배달의민족"),
    DAANGN("당근"),
    TOSS("토스"),
    NEXON("넥슨"),
    KRAFTON("크래프톤"),
    ;

    private final String korean;

    public static PredefinedCompany fromCode(String code) {
        for (var value : values()) {
            if (value.name().equals(code)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}

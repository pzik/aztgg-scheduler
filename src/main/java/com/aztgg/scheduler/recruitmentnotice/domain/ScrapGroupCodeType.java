package com.aztgg.scheduler.recruitmentnotice.domain;

import com.aztgg.scheduler.global.asset.PredefinedCompany;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 스크랩 그룹 단위로 hash값을 이용해 diff check 수행
 */
@Getter
@AllArgsConstructor
public enum ScrapGroupCodeType {

    NAVER(PredefinedCompany.NAVER),
    KAKAO(PredefinedCompany.KAKAO),
    KAKAO_GREETING(PredefinedCompany.KAKAO),
    KAKAO_GREETING_V12(PredefinedCompany.KAKAO),
    KAKAO_BANK(PredefinedCompany.KAKAO),
    WOOWAHAN(PredefinedCompany.WOOWAHAN),
    TOSS(PredefinedCompany.TOSS),
    LINE(PredefinedCompany.LINE),
    DAANGN(PredefinedCompany.DAANGN),
    COUPANG(PredefinedCompany.COUPANG),
    NEXON(PredefinedCompany.NEXON),
    KRAFTON(PredefinedCompany.KRAFTON),
    MOLOCO(PredefinedCompany.MOLOCO),
    DUNAMU(PredefinedCompany.DUNAMU),
    SENDBIRD(PredefinedCompany.SENDBIRD),
    ;

    private final PredefinedCompany company;
}

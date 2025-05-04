package com.aztgg.scheduler.company.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScrapGroupCodeType {

    NAVER(PredefinedCompany.NAVER),
    KAKAO(PredefinedCompany.KAKAO),
    KAKAO_BANK(PredefinedCompany.KAKAO),
    WOOWAHAN(PredefinedCompany.WOOWAHAN),
    TOSS(PredefinedCompany.TOSS),
    LINE(PredefinedCompany.LINE),
    DAANGN(PredefinedCompany.DAANGN),
    COUPANG(PredefinedCompany.COUPANG),
    ;

    private final PredefinedCompany company;
}

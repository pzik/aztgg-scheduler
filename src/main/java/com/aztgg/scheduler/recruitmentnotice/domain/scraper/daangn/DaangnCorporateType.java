package com.aztgg.scheduler.recruitmentnotice.domain.scraper.daangn;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DaangnCorporateType {

    KARROT_MARKET("당근"),
    KARROT_PAY("당근페이"),
    UNKNOWN("알수없음"),
    ;
    private final String departmentName;
}

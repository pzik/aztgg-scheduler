package com.aztgg.scheduler.recruitmentnotice.domain.scraper;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Tech JobGroupCode 내 직무(jobCodes)
 * 직무(콤마구분 (정보보안 - BA007008, 서버/백엔드 - BA007001, 프론트엔드 - BA007002, 데이터 - BA007005, SRE - BA007051, DBA - BA007006, QA - BA007010)
 */
@Getter
@AllArgsConstructor
public enum WoowahanTechJobCodeType {

    SECURITY("정보보안", "BA007008"),
    BACKEND("서버/백엔드", "BA007001"),
    FRONTEND("프론트엔드", "BA007002"),
    DATA("데이터", "BA007005"),
    SRE("SRE", "BA007051"),
    DBA("DBA", "BA007006"),
    QA("QA", "BA007010"),
    ;

    private final String korean;
    private final String code;
}

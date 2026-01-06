package com.aztgg.scheduler.recruitmentnotice.domain.scraper.woowahan;

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
    DATA2("데이터(AI/ML)", "BA007041"),
    SRE("SRE", "BA007051"),
    DBA("DBA", "BA007006"),
    QA("QA", "BA007010"),
    ROBOT("로보틱스", "BA007007"),
    RISK("리스크관리", "BA007049"),
    MARKETING("마케팅", "BA007018"),
    LAW("법무", "BA007027"),
    SECRETARY("비서", "BA007068"),
    BUSINESS("사업관리", "BA007022"),
    BUSINESS2("사업기획", "BA007016"),
    BUSINESS3("영업", "BA007019"),
    BUSINESS4("운영지원", "BA007038"),
    HR("인사", "BA007035"),
    BUSINESS5("통번역", "BA007052"),
    BUSINESS6("회계", "BA007030"),
    AMD("AMD", "BA007021"),
    BA("BA", "BA007059"),
    CS("CS", "BA007028"),
    PA("PA", "BA007064"),
    PM("PM", "BA007014"),
    ;

    private final String korean;
    private final String code;
}

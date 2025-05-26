package com.aztgg.scheduler.recruitmentnotice.application.dto;

import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNotice;
import org.springframework.util.StringUtils;

import java.util.Set;

public record CategoryClassifyRequestDto(Long recruitmentNoticeId, String jobOfferTitle, String companyCode, Set<String> categories) {

    public static CategoryClassifyRequestDto from(RecruitmentNotice recruitmentNotice) {
        return new CategoryClassifyRequestDto(recruitmentNotice.getRecruitmentNoticeId(),
                recruitmentNotice.getJobOfferTitle(),
                recruitmentNotice.getCompanyCode(),
                StringUtils.commaDelimitedListToSet(recruitmentNotice.getCategories()));
    }
}

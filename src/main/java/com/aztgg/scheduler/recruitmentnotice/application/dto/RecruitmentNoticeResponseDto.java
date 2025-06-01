package com.aztgg.scheduler.recruitmentnotice.application.dto;

import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNotice;
import lombok.Builder;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record RecruitmentNoticeResponseDto(Long recruitmentNoticeId,
                                           String companyCode,
                                           String jobOfferTitle,
                                           Set<String> corporateCodes,
                                           String standardCategory,
                                           LocalDateTime startAt,
                                           LocalDateTime endAt,
                                           LocalDateTime scrapedAt) {

    public static RecruitmentNoticeResponseDto from(RecruitmentNotice recruitmentNotice) {
        Set<String> corpCodes = StringUtils.commaDelimitedListToSet(recruitmentNotice.getCorporateCodes());
        return RecruitmentNoticeResponseDto.builder()
                .recruitmentNoticeId(recruitmentNotice.getRecruitmentNoticeId())
                .companyCode(recruitmentNotice.getCompanyCode())
                .jobOfferTitle(recruitmentNotice.getJobOfferTitle())
                .corporateCodes(corpCodes)
                .standardCategory(recruitmentNotice.getStandardCategory())
                .startAt(recruitmentNotice.getStartAt())
                .endAt(recruitmentNotice.getEndAt())
                .scrapedAt(recruitmentNotice.getScrapedAt())
                .build();
    }
}

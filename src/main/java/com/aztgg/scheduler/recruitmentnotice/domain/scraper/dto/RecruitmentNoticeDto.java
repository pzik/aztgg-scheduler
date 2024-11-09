package com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto;

import lombok.Builder;

public record RecruitmentNoticeDto(String jobOfferTitle,
                                   String url,
                                   String startAt,
                                   String endAt) {

    @Builder
    public RecruitmentNoticeDto {

    }
}

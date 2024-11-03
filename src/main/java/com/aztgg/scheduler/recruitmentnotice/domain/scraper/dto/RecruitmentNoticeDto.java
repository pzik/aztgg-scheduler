package com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto;

import lombok.Builder;

import java.time.LocalDateTime;

public record RecruitmentNoticeDto(String jobOfferTitle,
                                   String url,
                                   LocalDateTime startAt,
                                   LocalDateTime endAt) {

    @Builder
    public RecruitmentNoticeDto {

    }
}

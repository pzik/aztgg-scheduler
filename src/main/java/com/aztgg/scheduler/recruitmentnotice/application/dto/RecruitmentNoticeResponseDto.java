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

}

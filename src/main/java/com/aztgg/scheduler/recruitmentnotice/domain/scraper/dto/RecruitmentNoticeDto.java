package com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
public class RecruitmentNoticeDto {

    private String jobOfferTitle;
    private String url;
    private Set<String> corporateCodes;
    private Set<String> categories;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Builder
    public RecruitmentNoticeDto(String jobOfferTitle,
                                String url,
                                Set<String> categories,
                                Set<String> corporateCodes,
                                LocalDateTime startAt,
                                LocalDateTime endAt) {
        this.jobOfferTitle = jobOfferTitle;
        this.url = url;
        this.categories = categories;
        this.corporateCodes = corporateCodes;
        this.startAt = startAt;
        this.endAt = endAt;
        if (CollectionUtils.isEmpty(categories)) {
            this.categories = new HashSet<>();
        }
        if (CollectionUtils.isEmpty(corporateCodes)) {
            this.corporateCodes = new HashSet<>();
        }
    }

    public void addCategory(String category) {
        this.categories.add(category);
    }
}

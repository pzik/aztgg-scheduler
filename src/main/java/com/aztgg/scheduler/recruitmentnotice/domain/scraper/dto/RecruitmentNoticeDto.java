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
    private String hash;
    private Set<String> categories;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @Builder
    public RecruitmentNoticeDto(String jobOfferTitle, String url, String hash, Set<String> categories, LocalDateTime startAt, LocalDateTime endAt) {
        this.jobOfferTitle = jobOfferTitle;
        this.url = url;
        this.hash = hash;
        this.categories = categories;
        this.startAt = startAt;
        this.endAt = endAt;
        if (CollectionUtils.isEmpty(categories)) {
            this.categories = new HashSet<>();
        }
    }

    public void addCategory(String category) {
        this.categories.add(category);
    }
}

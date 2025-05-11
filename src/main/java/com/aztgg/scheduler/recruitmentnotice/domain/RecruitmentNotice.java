package com.aztgg.scheduler.recruitmentnotice.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Table("recruitment_notice")
public class RecruitmentNotice {

    @Id
    @Column("recruitmentNoticeId")
    private Long recruitmentNoticeId;

    @Column("companyCode")
    private String companyCode;

    @Column("scrapGroupCode")
    private String scrapGroupCode;

    @Column("jobOfferTitle")
    private String jobOfferTitle;

    @Column("hash")
    private String hash;

    @Column("corporateCodes")
    private String corporateCodes;

    @Column("categories")
    private String categories;

    @Column("url")
    private String url;

    @Column("clickCount")
    private int clickCount;

    @Column("scrapedAt")
    private LocalDateTime scrapedAt;

    @Column("startAt")
    private LocalDateTime startAt;

    @Column("endAt")
    private LocalDateTime endAt;

    @Builder
    public RecruitmentNotice(Long recruitmentNoticeId,
                             String companyCode,
                             String scrapGroupCode,
                             String jobOfferTitle,
                             String hash,
                             Set<String> categories,
                             Set<String> corporateCodes,
                             String url,
                             int clickCount,
                             LocalDateTime scrapedAt,
                             LocalDateTime startAt,
                             LocalDateTime endAt) {
        this.recruitmentNoticeId = recruitmentNoticeId;
        this.companyCode = companyCode;
        this.scrapGroupCode = scrapGroupCode;
        this.jobOfferTitle = jobOfferTitle;
        this.hash = hash;
        this.categories = StringUtils.arrayToCommaDelimitedString(categories.toArray());
        this.corporateCodes = StringUtils.arrayToCommaDelimitedString(corporateCodes.toArray());
        this.url = url;
        this.clickCount = clickCount;
        this.scrapedAt = scrapedAt;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public void updateRecruitmentNoticeId(Long recruitmentNoticeId) {
        this.recruitmentNoticeId = recruitmentNoticeId;
    }

    @Override
    public String toString() {
        return "RecruitmentNotice{" +
                "recruitmentNoticeId=" + recruitmentNoticeId +
                ", companyCode='" + companyCode + '\'' +
                ", scrapGroupCode='" + scrapGroupCode + '\'' +
                ", jobOfferTitle='" + jobOfferTitle + '\'' +
                ", hash='" + hash + '\'' +
                ", categories='" + categories + '\'' +
                ", corporateCodes='" + corporateCodes + '\'' +
                ", url='" + url + '\'' +
                ", clickCount=" + clickCount +
                ", scrapedAt=" + scrapedAt +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }
}

package com.aztgg.scheduler.recruitmentnotice.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Builder
@Table("recruitment_notice")
public class RecruitmentNotice {

    @Id
    @Column("recruitmentNoticeId")
    private Long recruitmentNoticeId;

    @Column("companyCode")
    private String companyCode;

    @Column("jobOfferTitle")
    private String jobOfferTitle;

    @Column("hash")
    private String hash;

    @Column("tag")
    private String tag;

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
                             String jobOfferTitle,
                             String hash,
                             String tag,
                             String url,
                             int clickCount,
                             LocalDateTime scrapedAt,
                             LocalDateTime startAt,
                             LocalDateTime endAt) {
        this.recruitmentNoticeId = recruitmentNoticeId;
        this.companyCode = companyCode;
        this.jobOfferTitle = jobOfferTitle;
        this.hash = hash;
        this.tag = tag;
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
                ", jobOfferTitle='" + jobOfferTitle + '\'' +
                ", hash='" + hash + '\'' +
                ", tag='" + tag + '\'' +
                ", url='" + url + '\'' +
                ", clickCount=" + clickCount +
                ", scrapedAt=" + scrapedAt +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                '}';
    }
}

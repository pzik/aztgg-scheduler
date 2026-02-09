package com.aztgg.scheduler.recruitmentnoticestatistic.domain.retryablerecruitmentnoticestatistic;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

/**
 * recruitmentNoticeId의 standardCategory는 createdAt 일자 통계에 재처리되야한다.
 */
@Getter
@Table("retryable_recruitment_notice_statistic")
public class RetryableRecruitmentNoticeStatistic {

    @Id
    @Column("retryableRecruitmentNoticeStatisticId")
    private Long retryableRecruitmentNoticeStatisticId;

    @Column("recruitmentNoticeId")
    private Long recruitmentNoticeId;

    @Column("createdAt")
    private LocalDate createdAt;

    @Builder
    public RetryableRecruitmentNoticeStatistic(Long retryableRecruitmentNoticeStatisticId, Long recruitmentNoticeId, LocalDate createdAt) {
        this.retryableRecruitmentNoticeStatisticId = retryableRecruitmentNoticeStatisticId;
        this.recruitmentNoticeId = recruitmentNoticeId;
        this.createdAt = createdAt;
        if (this.createdAt == null) {
            this.createdAt = LocalDate.now();
        }
    }
}

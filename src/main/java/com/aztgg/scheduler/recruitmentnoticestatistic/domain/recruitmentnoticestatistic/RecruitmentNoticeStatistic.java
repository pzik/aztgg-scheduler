package com.aztgg.scheduler.recruitmentnoticestatistic.domain.recruitmentnoticestatistic;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

/**
 * standardCategory 별 공고 카운트 통계 정보, 회사별 검색 가능
 */
@Getter
@Table("recruitment_notice_statistic")
public class RecruitmentNoticeStatistic {

    @Id
    @Column("recruitmentNoticeStatisticId")
    private Long recruitmentNoticeStatisticId;

    @Column("standardCategory")
    private String standardCategory;

    @Column("companyCode")
    private String companyCode;

    @Column("count")
    private Integer count;

    @Column("createdAt")
    private LocalDate createdAt;

    @Builder
    public RecruitmentNoticeStatistic(Long recruitmentNoticeStatisticId, String standardCategory, String companyCode, Integer count, LocalDate createdAt) {
        this.recruitmentNoticeStatisticId = recruitmentNoticeStatisticId;
        this.standardCategory = standardCategory;
        this.companyCode = companyCode;
        this.count = count;
        this.createdAt = createdAt;
        if (this.createdAt == null) {
            this.createdAt = LocalDate.now();
        }
    }

    public void increaseCount() {
        this.count++;
    }
}

package com.aztgg.scheduler.recruitmentnoticestatistic.domain.recruitmentnoticestatistic;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecruitmentNoticeStatisticRepository extends CrudRepository<RecruitmentNoticeStatistic, Long> {

    @Query("SELECT * FROM recruitment_notice_statistic WHERE companyCode = :companyCode AND standardCategory = :standardCategory AND createdAt = :createAt")
    Optional<RecruitmentNoticeStatistic> findByCompanyCodeAndStandardCategoryAndCreatedAt(
            @Param("companyCode") String companyCode,
            @Param("standardCategory") String standardCategory,
            @Param("createAt") LocalDate createAt
    );

    List<RecruitmentNoticeStatistic> findAllByCompanyCodeAndCreatedAt(String companyCode, LocalDate createAt);
}

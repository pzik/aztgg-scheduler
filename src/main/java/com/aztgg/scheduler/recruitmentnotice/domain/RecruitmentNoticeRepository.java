package com.aztgg.scheduler.recruitmentnotice.domain;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RecruitmentNoticeRepository extends CrudRepository<RecruitmentNotice, Long> {

    List<RecruitmentNotice> findByScrapGroupCode(String scrapGroupCode);

    List<RecruitmentNotice> findAllByCompanyCode(String companyCode);

    @Query("SELECT r.* FROM recruitment_notice r WHERE r.standardCategory IS NULL OR r.standardCategory = ''")
    List<RecruitmentNotice> findAllStandardCategoryIsNullOrEmpty();

    @Query("SELECT r.* FROM recruitment_notice r WHERE r.scrapedAt >= :scrapedAt ORDER BY r.scrapedAt DESC")
    List<RecruitmentNotice> findAllByScrapedAtAfterOrderByScrapedAtDesc(@Param("scrapedAt") LocalDateTime scrapedAt);
}

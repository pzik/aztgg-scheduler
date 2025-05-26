package com.aztgg.scheduler.recruitmentnotice.domain;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RecruitmentNoticeRepository extends CrudRepository<RecruitmentNotice, Long> {

    List<RecruitmentNotice> findByScrapGroupCode(String scrapGroupCode);

    @Query("SELECT r.* FROM recruitment_notice r WHERE r.standardCategory IS NULL OR r.standardCategory = ''")
    List<RecruitmentNotice> findAllStandardCategoryIsNullOrEmpty();
}

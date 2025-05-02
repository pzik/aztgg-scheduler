package com.aztgg.scheduler.recruitmentnotice.domain;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecruitmentNoticeRepository extends CrudRepository<RecruitmentNotice, Long> {

    List<RecruitmentNotice> findByScrapGroupCode(String scrapGroupCode);
}

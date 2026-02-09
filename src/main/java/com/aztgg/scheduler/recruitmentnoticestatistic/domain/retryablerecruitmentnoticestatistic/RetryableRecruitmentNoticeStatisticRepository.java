package com.aztgg.scheduler.recruitmentnoticestatistic.domain.retryablerecruitmentnoticestatistic;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RetryableRecruitmentNoticeStatisticRepository extends CrudRepository<RetryableRecruitmentNoticeStatistic, Long> {

    List<RetryableRecruitmentNoticeStatistic> findAll();
}

package com.aztgg.scheduler.recruitmentnoticestatistic.domain;

import com.aztgg.scheduler.recruitmentnoticestatistic.domain.recruitmentnoticestatistic.RecruitmentNoticeStatistic;
import com.aztgg.scheduler.recruitmentnoticestatistic.domain.recruitmentnoticestatistic.RecruitmentNoticeStatisticRepository;
import com.aztgg.scheduler.recruitmentnoticestatistic.domain.retryablerecruitmentnoticestatistic.RetryableRecruitmentNoticeStatistic;
import com.aztgg.scheduler.recruitmentnoticestatistic.domain.retryablerecruitmentnoticestatistic.RetryableRecruitmentNoticeStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitmentNoticeStatisticDomainService {

    private final RetryableRecruitmentNoticeStatisticRepository retryableRecruitmentNoticeStatisticRepository;
    private final RecruitmentNoticeStatisticRepository recruitmentNoticeStatisticRepository;

    @Transactional
    public void markRetryItems(List<Long> retryableNotices, LocalDate date) {
        List<RetryableRecruitmentNoticeStatistic> entities = retryableNotices.stream()
                .map(id -> RetryableRecruitmentNoticeStatistic.builder()
                        .recruitmentNoticeId(id)
                        .createdAt(date)
                        .build())
                .toList();
        retryableRecruitmentNoticeStatisticRepository.saveAll(entities);
    }

    @Transactional
    public void saveAll(List<RecruitmentNoticeStatistic> statistics) {
        recruitmentNoticeStatisticRepository.saveAll(statistics);
    }

    public List<RetryableRecruitmentNoticeStatistic> findAllRetryableItems() {
        return retryableRecruitmentNoticeStatisticRepository.findAll();
    }

    public List<RecruitmentNoticeStatistic> findAllRetryableItemsByCreatedAt(LocalDate createAt) {
        return recruitmentNoticeStatisticRepository.findAllByCreatedAt(createAt);
    }

    @Transactional
    public void deleteRetryableItem(RetryableRecruitmentNoticeStatistic item) {
        retryableRecruitmentNoticeStatisticRepository.delete(item);
    }

    @Transactional
    public void deleteStatisticItems(Collection<RecruitmentNoticeStatistic> items) {
        recruitmentNoticeStatisticRepository.deleteAll(items);
    }

    @Transactional
    public void updateStatisticIncreaseCount(String companyCode, String standardCategory, LocalDate date) {
        Optional<RecruitmentNoticeStatistic> statisticOpt = recruitmentNoticeStatisticRepository.findByCompanyCodeAndStandardCategoryAndCreatedAt(
                companyCode,
                standardCategory,
                date
        );

        if (statisticOpt.isPresent()) {
            RecruitmentNoticeStatistic statistic = statisticOpt.get();
            statistic.increaseCount();
            recruitmentNoticeStatisticRepository.save(statistic);
        } else {
            RecruitmentNoticeStatistic newStatistic = RecruitmentNoticeStatistic.builder()
                    .companyCode(companyCode)
                    .standardCategory(standardCategory)
                    .count(1)
                    .createdAt(date)
                    .build();
            recruitmentNoticeStatisticRepository.save(newStatistic);
        }
    }
}

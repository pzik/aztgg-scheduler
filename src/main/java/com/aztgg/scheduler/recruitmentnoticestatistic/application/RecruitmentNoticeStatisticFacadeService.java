package com.aztgg.scheduler.recruitmentnoticestatistic.application;

import com.aztgg.scheduler.global.asset.PredefinedCompany;
import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNotice;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeDomainService;
import com.aztgg.scheduler.recruitmentnoticestatistic.domain.RecruitmentNoticeStatisticDomainService;
import com.aztgg.scheduler.recruitmentnoticestatistic.domain.recruitmentnoticestatistic.RecruitmentNoticeStatistic;
import com.aztgg.scheduler.recruitmentnoticestatistic.domain.retryablerecruitmentnoticestatistic.RetryableRecruitmentNoticeStatistic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentNoticeStatisticFacadeService {

    private final RecruitmentNoticeStatisticDomainService recruitmentNoticeStatisticDomainService;
    private final RecruitmentNoticeDomainService recruitmentNoticeDomainService;

    public void collect(LocalDate collectDate) {
        // 이미 존재하는 통계는 삭제
        List<RecruitmentNoticeStatistic> before = recruitmentNoticeStatisticDomainService.findAllRetryableItemsByCreatedAt(collectDate);
        if (!before.isEmpty()) {
            AppLogger.infoLog(collectDate + " 일자 이전 통계 기록이 있어 삭제 후 재처리합니다");
            recruitmentNoticeStatisticDomainService.deleteStatisticItems(before);
        }

        for (var company : PredefinedCompany.values()) {
            AppLogger.infoLog(company + " 통계 수집 시작, 일자 = " + collectDate);
            List<RecruitmentNotice> recruitmentNoticeList = recruitmentNoticeDomainService.findAllByCompanyCode(company.name());
            // standardCategory 정의가 안된애들은 재처리로 이동
            List<Long> retryableNotices = recruitmentNoticeList.stream()
                    .filter(a -> !StringUtils.hasText(a.getStandardCategory()))
                    .map(RecruitmentNotice::getRecruitmentNoticeId)
                    .toList();

            if (!retryableNotices.isEmpty()) {
                recruitmentNoticeStatisticDomainService.markRetryItems(retryableNotices, collectDate);
            }

            // 정형화 직무별로 구분하고 통계 기록
            Map<String, Long> targets = recruitmentNoticeList.stream()
                    .filter(a -> !retryableNotices.contains(a.getRecruitmentNoticeId()))
                    .collect(Collectors.groupingBy(RecruitmentNotice::getStandardCategory, Collectors.counting()));

            List<RecruitmentNoticeStatistic> statistics = targets.entrySet().stream()
                    .map(entry -> RecruitmentNoticeStatistic.builder()
                            .companyCode(company.name())
                            .standardCategory(entry.getKey())
                            .count(entry.getValue().intValue())
                            .build())
                    .toList();

            recruitmentNoticeStatisticDomainService.saveAll(statistics);
            AppLogger.infoLog(company + " 통계 수집 끝, 대상 데이터 " + recruitmentNoticeList.size()
                    + " 그 중 재처리 필요 " + retryableNotices.size());
        }
    }

    public void retryCollect() {
        AppLogger.infoLog("통계 재처리 프로세스 시작");
        List<RetryableRecruitmentNoticeStatistic> retryableItems = recruitmentNoticeStatisticDomainService.findAllRetryableItems();

        for (RetryableRecruitmentNoticeStatistic item : retryableItems) {
            Optional<RecruitmentNotice> noticeOpt = recruitmentNoticeDomainService.findById(item.getRecruitmentNoticeId());

            // 삭제된 공고는 스킵처리
            if (noticeOpt.isEmpty()) {
                recruitmentNoticeStatisticDomainService.deleteRetryableItem(item);
                continue;
            }

            RecruitmentNotice notice = noticeOpt.get();
            // 아직 AI 정형화가 안됐다면 안됐다면 스킵하고 다음에 처리
            if (!StringUtils.hasText(notice.getStandardCategory())) {
                continue;
            }

            // 재처리 수행
            recruitmentNoticeStatisticDomainService.updateStatisticIncreaseCount(
                    notice.getCompanyCode(),
                    notice.getStandardCategory(),
                    item.getCreatedAt()
            );
            recruitmentNoticeStatisticDomainService.deleteRetryableItem(item);
        }
        AppLogger.infoLog("통계 재처리 프로세스 끝");
    }
}

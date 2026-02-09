package com.aztgg.scheduler.recruitmentnoticestatistic.presentation;

import com.aztgg.scheduler.recruitmentnoticestatistic.application.RecruitmentNoticeStatisticFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RecruitmentNoticeStatisticScheduler {

    private final RecruitmentNoticeStatisticFacadeService recruitmentNoticeStatisticFacadeService;

    /**
     * 매일 한 번 23시에 통계 정보 수집한다.
     * standardCategory 정의가 되지 않은 공고들은 재처리 항목으로 기록한다.
     */
//    @Scheduled(cron = "0 1 23 * * *", zone = "Asia/Seoul")
    @Scheduled(fixedDelay = 4_3200_000)
    public void collectStatistics() {
        recruitmentNoticeStatisticFacadeService.collect(LocalDate.now());
    }

    /**
     * 공고 통계 재처리 프로세스
     * 재처리 항목 공고를 다시 확인해 해당 일자 통계 정보를 갱신한다.
     */
    @Scheduled(cron = "0 10 23 * * *", zone = "Asia/Seoul")
    public void retryCollectStatistics() {
        recruitmentNoticeStatisticFacadeService.retryCollect();
    }
}

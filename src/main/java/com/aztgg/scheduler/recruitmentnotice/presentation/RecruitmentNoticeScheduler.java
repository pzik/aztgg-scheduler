package com.aztgg.scheduler.recruitmentnotice.presentation;

import com.aztgg.scheduler.recruitmentnotice.application.KakaoDesignerRecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.application.KakaoDevRecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.application.TossNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.application.WoowahanNoticeCollectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecruitmentNoticeScheduler {

    private final KakaoDevRecruitmentNoticeCollectorService kakaoDevRecruitmentNoticeCollectorService;
    private final KakaoDesignerRecruitmentNoticeCollectorService kakaoDesignerRecruitmentNoticeCollectorService;
    private final WoowahanNoticeCollectorService woowahanNoticeCollectorService;
    private final TossNoticeCollectorService tossNoticeCollectorService;

    @Scheduled(fixedDelay = 30_000)
    public void collectKakaoDevNotices() {
        kakaoDevRecruitmentNoticeCollectorService.collect();
    }

    @Scheduled(fixedDelay = 30_000)
    public void collectKakaoDesignerNotices() {
        kakaoDesignerRecruitmentNoticeCollectorService.collect();
    }

    @Scheduled(fixedDelay = 30_000)
    public void collectWoowahanDevNotices() {
        woowahanNoticeCollectorService.collect();
    }

    @Scheduled(fixedDelay = 180_000)
    public void collectTossTotalNotices() {
        tossNoticeCollectorService.collect();
    }
}

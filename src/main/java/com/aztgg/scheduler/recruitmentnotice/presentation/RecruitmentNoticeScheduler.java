package com.aztgg.scheduler.recruitmentnotice.presentation;

import com.aztgg.scheduler.recruitmentnotice.application.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecruitmentNoticeScheduler {

    private final KakaoCorpNoticeCollectorService kakaoCorpNoticeCollectorService;
    private final WoowahanNoticeCollectorService woowahanNoticeCollectorService;
    private final TossNoticeCollectorService tossNoticeCollectorService;
    private final LineNoticeCollectorService lineNoticeCollectorService;
    private final DaangnNoticeCollectorService daangnNoticeCollectorService;
    private final KakaoBankNoticeCollectorService kakaoBankNoticeCollectorService;
    private final NaverNoticeControllerService naverNoticeControllerService;
    private final CoupangNoticeControllerService coupangNoticeControllerService;
    private final KakaoGreetingNoticeCollectorService kakaoGreetingNoticeCollectorService;

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKakaoNotices() {
        kakaoCorpNoticeCollectorService.collect();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectWoowahanDevNotices() {
        woowahanNoticeCollectorService.collect();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectTossTotalNotices() {
        tossNoticeCollectorService.collect();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectLineTotalNotices() {
        lineNoticeCollectorService.collect();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectDaangnTotalNotices() {
        daangnNoticeCollectorService.collect();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKakaoBankTotalNotices() {
        kakaoBankNoticeCollectorService.collect();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKakaoGreetingNotices() {
        kakaoGreetingNoticeCollectorService.collect();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectNaverTotalNotices() {
        naverNoticeControllerService.collect();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectCoupangTotalNotices() {
        coupangNoticeControllerService.collect();
    }
}

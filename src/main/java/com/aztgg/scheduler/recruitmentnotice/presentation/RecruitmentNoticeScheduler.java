package com.aztgg.scheduler.recruitmentnotice.presentation;

import com.aztgg.scheduler.recruitmentnotice.application.AiCategoryClassifierService;
import com.aztgg.scheduler.recruitmentnotice.application.collectorservice.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final AiCategoryClassifierService aiCategoryClassifierService;

    @Value("${aztgg.donotnotice}")
    private boolean doNotCollect;

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKakaoNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        kakaoCorpNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectWoowahanDevNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        woowahanNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectTossTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        tossNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectLineTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        lineNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectDaangnTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        daangnNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKakaoBankTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        kakaoBankNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKakaoGreetingNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        kakaoGreetingNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectNaverTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        naverNoticeControllerService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectCoupangTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        coupangNoticeControllerService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }
}

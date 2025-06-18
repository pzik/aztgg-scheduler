package com.aztgg.scheduler.recruitmentnotice.presentation;

import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.application.AiCategoryClassifierService;
import com.aztgg.scheduler.recruitmentnotice.application.collectorservice.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecruitmentNoticeScheduler {

    private final KakaoCorpNoticeCollectorService kakaoCorpNoticeCollectorService;
    private final WoowahanNoticeCollectorService woowahanNoticeCollectorService;
    private final TossNoticeCollectorService tossNoticeCollectorService;
    private final LineNoticeCollectorService lineNoticeCollectorService;
    private final DaangnNoticeCollectorService daangnNoticeCollectorService;
    private final KakaoBankNoticeCollectorService kakaoBankNoticeCollectorService;
    private final NaverNoticeCollectorService naverNoticeCollectorService;
    private final CoupangNoticeCollectorService coupangNoticeCollectorService;
    private final KakaoGreetingNoticeCollectorService kakaoGreetingNoticeCollectorService;
    private final NexonNoticesCollectorService nexonNoticesCollectorService;
    private final KraftonNoticesCollectorService kraftonNoticesCollectorService;
    private final MolocoNoticeCollectorService molocoNoticeCollectorService;
    private final DunamuNoticeCollectorService dunamuNoticeCollectorService;
    private final SendbirdNoticeCollectorService sendbirdNoticeCollectorService;

    private final AiCategoryClassifierService aiCategoryClassifierService;

    @Value("${aztgg.donotnotice:false}")
    private boolean doNotCollect;

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKakaoNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        kakaoCorpNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectWoowahanDevNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        woowahanNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectTossTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        tossNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectLineTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        lineNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectDaangnTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        daangnNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKakaoBankTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        kakaoBankNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKakaoGreetingNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        kakaoGreetingNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectNaverTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        naverNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectCoupangTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        coupangNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectNexonTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        nexonNoticesCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKraftonTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        kraftonNoticesCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectMolocoTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        molocoNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectDunamuTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        dunamuNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectSendbirdTotalNotices() {
        if (doNotCollect) {
            AppLogger.debugLog("aztgg.donotnotice = true");
            return;
        }
        sendbirdNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }
}

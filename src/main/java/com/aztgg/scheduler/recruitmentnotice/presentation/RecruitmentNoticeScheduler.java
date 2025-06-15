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
        naverNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectCoupangTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        coupangNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectNexonTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        nexonNoticesCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectKraftonTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        kraftonNoticesCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectMolocoTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        molocoNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectDunamuTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        dunamuNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }

    @Scheduled(fixedDelay = 4_3200_000)
    public void collectSendbirdTotalNotices() {
        if (doNotCollect) {
            log.debug("aztgg.donotnotice = true");
            return;
        }
        sendbirdNoticeCollectorService.collect();
        aiCategoryClassifierService.classifyingNoticeCategories();
    }
}

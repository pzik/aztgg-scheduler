package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.global.service.DiscordWebhookService;
import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakaobank.KakaoBankNoticesScraper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class KakaoBankNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient kakaoBankCareersPublicRestClient;

    public KakaoBankNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                           RestClient kakaoBankCareersPublicRestClient) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.KAKAO_BANK);
        this.kakaoBankCareersPublicRestClient = kakaoBankCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new KakaoBankNoticesScraper(kakaoBankCareersPublicRestClient);
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            AppLogger.errorLog("unexpected exception", e);
        }
        return scrapResult;
    }
}

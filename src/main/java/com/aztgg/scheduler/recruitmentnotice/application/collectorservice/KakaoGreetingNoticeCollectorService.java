package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakaogreeting.KakaoGreetingv11Scraper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class KakaoGreetingNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient baseRestClient;

    public KakaoGreetingNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                               RestClient baseRestClient) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.KAKAO_GREETING);
        this.baseRestClient = baseRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new KakaoGreetingv11Scraper(baseRestClient);
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            AppLogger.errorLog("unexpected exception", e);
        }
        return scrapResult;
    }
}

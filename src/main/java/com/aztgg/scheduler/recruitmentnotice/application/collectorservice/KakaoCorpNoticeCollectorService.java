package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakao.KakaoNoticesScraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KakaoCorpNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient kakaoCareersPublicRestClient;

    public KakaoCorpNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                           RestClient kakaoCareersPublicRestClient) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.KAKAO);
        this.kakaoCareersPublicRestClient = kakaoCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();

        try {
            Scraper<List<RecruitmentNoticeDto>> scraper = new KakaoNoticesScraper(kakaoCareersPublicRestClient);
            List<RecruitmentNoticeDto> noticeList = scraper.scrap();
            scrapResult.addAll(noticeList);
        } catch (Exception e) {
            log.error("unexpected exception", e);
        }
        return scrapResult;
    }
}

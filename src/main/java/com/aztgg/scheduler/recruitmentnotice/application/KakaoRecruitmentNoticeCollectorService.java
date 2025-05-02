package com.aztgg.scheduler.recruitmentnotice.application;

import com.aztgg.scheduler.company.domain.PredefinedCompany;
import com.aztgg.scheduler.global.crawler.Scraper;
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
public class KakaoRecruitmentNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient kakaoCareersPublicRestClient;

    public KakaoRecruitmentNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                                  RestClient kakaoCareersPublicRestClient) {
        super(recruitmentNoticeRepository, PredefinedCompany.KAKAO);
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

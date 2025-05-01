package com.aztgg.scheduler.recruitmentnotice.application;

import com.aztgg.scheduler.company.domain.PredefinedCompany;
import com.aztgg.scheduler.global.crawler.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakao.KakaoNoticesScraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakao.KakaoPartType;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class KakaoDesignerRecruitmentNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient kakaoCareersPublicRestClient;

    public KakaoDesignerRecruitmentNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                                          RestClient kakaoCareersPublicRestClient) {
        super(recruitmentNoticeRepository, PredefinedCompany.KAKAO);
        this.kakaoCareersPublicRestClient = kakaoCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        int page = 1;
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();

        while (true) {
            try {
                Scraper<List<RecruitmentNoticeDto>> scraper = new KakaoNoticesScraper(kakaoCareersPublicRestClient, page, KakaoPartType.DESIGN);
                List<RecruitmentNoticeDto> noticeList = scraper.scrap();
                if (noticeList.isEmpty()) {
                    break;
                }
                scrapResult.addAll(noticeList);
                page++;
                Thread.sleep(1000); // 1초 쉬고
            } catch (Exception e) {
                log.error("unexpected exception", e);
                break;
            }
        }

        return scrapResult;
    }
}

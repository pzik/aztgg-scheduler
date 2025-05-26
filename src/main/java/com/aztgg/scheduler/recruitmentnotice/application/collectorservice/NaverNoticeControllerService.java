package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.company.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.naver.NaverNoticesScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class NaverNoticeControllerService extends RecruitmentNoticeCollectorService{
    private final RestClient naverCareersPublicRestClient;

    public NaverNoticeControllerService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                        RestClient naverCareersPublicRestClient) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.NAVER);
        this.naverCareersPublicRestClient = naverCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new NaverNoticesScraper(naverCareersPublicRestClient);
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            log.error("unexpected exception", e);
        }
        return scrapResult;
    }

}

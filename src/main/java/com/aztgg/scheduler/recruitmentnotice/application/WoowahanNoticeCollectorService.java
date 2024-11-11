package com.aztgg.scheduler.recruitmentnotice.application;

import com.aztgg.scheduler.company.domain.PredefinedCompany;
import com.aztgg.scheduler.global.crawler.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.WoowahanNoticesScraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WoowahanNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient woowahanCareersPublicRestClient;

    public WoowahanNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                          RestClient woowahanCareersPublicRestClient) {
        super(recruitmentNoticeRepository, PredefinedCompany.WOOWAHAN);
        this.woowahanCareersPublicRestClient = woowahanCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new WoowahanNoticesScraper(woowahanCareersPublicRestClient);
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            log.error("unexpected exception", e);
        }
        return scrapResult;
    }
}

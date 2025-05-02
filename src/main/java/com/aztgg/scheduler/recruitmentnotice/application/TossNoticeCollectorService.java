package com.aztgg.scheduler.recruitmentnotice.application;

import com.aztgg.scheduler.company.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.toss.TossTotalNoticesScraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TossNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient tossCareersPublicRestClient;

    public TossNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                          RestClient tossCareersPublicRestClient) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.TOSS);
        this.tossCareersPublicRestClient = tossCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new TossTotalNoticesScraper(tossCareersPublicRestClient);
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            log.error("unexpected exception", e);
        }
        return scrapResult;
    }
}

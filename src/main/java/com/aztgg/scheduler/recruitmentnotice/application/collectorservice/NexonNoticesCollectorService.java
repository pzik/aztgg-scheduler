package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.nexon.NexonNoticesScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NexonNoticesCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient nexonCareersPublicRestClient;

    public NexonNoticesCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                        RestClient nexonCareersPublicRestClient) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.NEXON);
        this.nexonCareersPublicRestClient = nexonCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new NexonNoticesScraper(nexonCareersPublicRestClient);
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            log.error("unexpected exception", e);
        }
        return scrapResult;
    }
}

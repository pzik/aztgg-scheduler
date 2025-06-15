package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.moloco.MolocoNoticesScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MolocoNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient molocoCareersPublicRestClient;

    public MolocoNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                         RestClient molocoCareersPublicRestClient) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.MOLOCO);
        this.molocoCareersPublicRestClient = molocoCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new MolocoNoticesScraper(molocoCareersPublicRestClient);
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            log.error("unexpected exception", e);
        }
        return scrapResult;
    }
}
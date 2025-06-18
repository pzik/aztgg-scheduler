package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.daangn.DaangnNoticesScraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class DaangnNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient daangnCareersPublicRestClient;

    public DaangnNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                        RestClient daangnCareersPublicRestClient) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.DAANGN);
        this.daangnCareersPublicRestClient = daangnCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new DaangnNoticesScraper(daangnCareersPublicRestClient);
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            AppLogger.errorLog("unexpected exception", e);
        }
        return scrapResult;
    }
}

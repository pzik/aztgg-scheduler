package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.line.LineNoticesScraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class LineNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient lineCareersPublicRestClient;

    public LineNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                                     RestClient lineCareersPublicRestClient) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.LINE);
        this.lineCareersPublicRestClient = lineCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new LineNoticesScraper(lineCareersPublicRestClient);
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            AppLogger.errorLog("unexpected exception", e);
        }
        return scrapResult;
    }
}

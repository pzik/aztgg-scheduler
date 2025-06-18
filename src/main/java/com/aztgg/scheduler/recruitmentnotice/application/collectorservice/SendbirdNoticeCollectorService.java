package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.sendbird.SendbirdNoticesScraper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class SendbirdNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient sendbirdCareersPublicRestClient;

    public SendbirdNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                         RestClient sendbirdCareersPublicRestClient) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.SENDBIRD);
        this.sendbirdCareersPublicRestClient = sendbirdCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        try {
            Scraper<List<RecruitmentNoticeDto>> scraper = new SendbirdNoticesScraper(sendbirdCareersPublicRestClient);
            return scraper.scrap();
        } catch (Exception e) {
            AppLogger.errorLog("Error occurred while scraping Sendbird job postings", e);
            return new ArrayList<>();
        }
    }
} 
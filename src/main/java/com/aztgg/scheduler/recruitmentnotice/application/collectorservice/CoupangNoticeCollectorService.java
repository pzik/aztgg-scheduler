package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.coupang.CoupangNoticesScraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CoupangNoticeCollectorService extends RecruitmentNoticeCollectorService {

    public CoupangNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                          ApplicationEventPublisher applicationEventPublisher) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.COUPANG);
    }
    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new CoupangNoticesScraper();
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            AppLogger.errorLog("unexpected exception", e);
        }
        return scrapResult;
    }
}

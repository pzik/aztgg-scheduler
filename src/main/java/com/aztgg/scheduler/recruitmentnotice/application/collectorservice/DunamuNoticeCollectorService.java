package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dunamu.DunamuNoticesScraper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DunamuNoticeCollectorService extends RecruitmentNoticeCollectorService {

    public DunamuNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.DUNAMU);
    }
    
    @Override
    protected List<RecruitmentNoticeDto> result() {
        try {
            Scraper<List<RecruitmentNoticeDto>> scraper = new DunamuNoticesScraper();
            return scraper.scrap();
        } catch (Exception e) {
            AppLogger.errorLog("Error occurred while scraping Dunamu job postings", e);
            return new ArrayList<>();
        }
    }
}
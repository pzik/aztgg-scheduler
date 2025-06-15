package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dunamu.DunamuNoticesScraper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
            log.error("Error occurred while scraping Dunamu job postings", e);
            return new ArrayList<>();
        }
    }
}
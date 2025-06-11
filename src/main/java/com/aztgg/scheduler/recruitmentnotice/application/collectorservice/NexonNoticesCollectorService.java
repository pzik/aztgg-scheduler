package com.aztgg.scheduler.recruitmentnotice.application.collectorservice;

import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeCollectorService;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.nexon.NexonNoticesScraper;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NexonNoticesCollectorService extends RecruitmentNoticeCollectorService {

    private final WebDriver nexonWebDriver;

    public NexonNoticesCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                        @Qualifier("nexonWebDriver") WebDriver nexonWebDriver) {
        super(recruitmentNoticeRepository, ScrapGroupCodeType.NEXON);
        this.nexonWebDriver = nexonWebDriver;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        Scraper<List<RecruitmentNoticeDto>> scraper = new NexonNoticesScraper(nexonWebDriver);
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();
        try {
            scrapResult.addAll(scraper.scrap());
        } catch (Exception e) {
            log.error("unexpected exception", e);
        }
        return scrapResult;
    }
}

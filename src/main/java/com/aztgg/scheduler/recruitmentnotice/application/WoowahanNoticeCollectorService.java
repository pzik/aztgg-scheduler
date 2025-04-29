package com.aztgg.scheduler.recruitmentnotice.application;

import com.aztgg.scheduler.company.domain.PredefinedCompany;
import com.aztgg.scheduler.global.crawler.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.WoowahanTechJobCodeType;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.WoowahanTechJobGroupCodeNoticesScraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class WoowahanNoticeCollectorService extends RecruitmentNoticeCollectorService {

    private final RestClient woowahanCareersPublicRestClient;

    public WoowahanNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                          RestClient woowahanCareersPublicRestClient) {
        super(recruitmentNoticeRepository, PredefinedCompany.WOOWAHAN);
        this.woowahanCareersPublicRestClient = woowahanCareersPublicRestClient;
    }

    @Override
    protected List<RecruitmentNoticeDto> result() {
        List<RecruitmentNoticeDto> scrapResult = new ArrayList<>();

        // jobCode별로 n번 호출
        for (var jobCodeType : WoowahanTechJobCodeType.values()) {
            Scraper<List<RecruitmentNoticeDto>> scraper = new WoowahanTechJobGroupCodeNoticesScraper(woowahanCareersPublicRestClient, jobCodeType.getCode());
            try {
                List<RecruitmentNoticeDto> result = scraper.scrap();
                // category 업데이트 (API 필드론 알 수 없음)
                for (var notice : result) {
                    notice.addCategory(jobCodeType.getKorean());
                }
                scrapResult.addAll(result);
            } catch (Exception e) {
                log.error("unexpected exception", e);
            }
        }
        return scrapResult;
    }
}

package com.aztgg.scheduler.recruitmentnotice.domain.scraper;

import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

// todo 구체화

@SpringBootTest
public class NaverNoticesScraperTest {

    @Autowired
    private RestClient.Builder commonRestClient;

    @Test
    void testScrap() throws IOException {
        // given
        NaverNoticesScraper scraper = new NaverNoticesScraper(commonRestClient);

        // when
        List<RecruitmentNoticeDto> recuitmentNoticeDtoList = scraper.scrap();
        for (var i : recuitmentNoticeDtoList) {
            System.out.println(i);
        }
        // then
    }
}

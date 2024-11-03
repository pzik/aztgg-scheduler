package com.aztgg.scheduler.recruitmentnotice.domain.scraper;

import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.KakaoRecruitmentNoticeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KakaoNoticesScraperTest { // TODO : 임시용, 개선

    @Autowired
    private RestClient kakaoCareersPublicRestClient;

    @Test
    void testScrap() throws IOException {
        // given
        int page = 1;
        KakaoPartType partType = KakaoPartType.TECHNOLOGY;
        KakaoNoticesScraper scraper = new KakaoNoticesScraper(kakaoCareersPublicRestClient, 1, partType);

        // when
        List<KakaoRecruitmentNoticeDto> recruitmentNoticeDtoList = scraper.scrap();
        for (var i : recruitmentNoticeDtoList) {
            System.out.println(i);
        }
    }
}
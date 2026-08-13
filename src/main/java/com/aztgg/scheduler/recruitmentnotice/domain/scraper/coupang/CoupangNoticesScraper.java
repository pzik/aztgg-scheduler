package com.aztgg.scheduler.recruitmentnotice.domain.scraper.coupang;

import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CoupangNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String API_URL = "https://boards-api.greenhouse.io/v1/boards/coupang/jobs";
    private static final String JOB_URL_PREFIX = "https://www.coupang.jobs/kr/jobs/?gh_jid=";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 쿠팡 공고 스크랩 (지역 : 서울, 대한민국)
     * Greenhouse 공개 API 사용
     */
    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        String json = Jsoup.connect(API_URL)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
                .ignoreContentType(true)
                .timeout(15000)
                .execute()
                .body();

        JsonNode root = OBJECT_MAPPER.readTree(json);
        JsonNode jobs = root.get("jobs");

        List<RecruitmentNoticeDto> results = new ArrayList<>();
        if (jobs == null || jobs.isEmpty()) {
            return results;
        }

        for (JsonNode job : jobs) {
            String location = job.path("location").path("name").asText("");
            if (!isKoreanLocation(location)) {
                continue;
            }

            String title = job.get("title").asText();
            String id = job.get("id").asText();
            String url = JOB_URL_PREFIX + id;

            results.add(RecruitmentNoticeDto.builder()
                    .jobOfferTitle(title)
                    .url(url)
                    .build());
        }

        return results;
    }

    private boolean isKoreanLocation(String location) {
        return (location.contains("Seoul") || location.contains("South Korea"))
                && !location.contains("z-Test");
    }
}

package com.aztgg.scheduler.recruitmentnotice.domain.scraper.daangn;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DaangnNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String DAANGN_BOARD_URL = "https://boards-api.greenhouse.io/v1/boards/daangn/jobs";
    private static final String DAANGNPAY_BOARD_URL = "https://boards-api.greenhouse.io/v1/boards/daangnpay/jobs";
    private static final String JOB_URL_PREFIX = "https://careers.daangn.com/jobs/role/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        List<RecruitmentNoticeDto> results = new ArrayList<>();
        results.addAll(fetchBoard(DAANGN_BOARD_URL));
        results.addAll(fetchBoard(DAANGNPAY_BOARD_URL));
        return results;
    }

    private List<RecruitmentNoticeDto> fetchBoard(String apiUrl) throws IOException {
        String json = Jsoup.connect(apiUrl)
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
            String id = job.get("id").asText();
            String title = job.get("title").asText();
            String url = JOB_URL_PREFIX + id + "/";

            String corporateValue = getMetadataValue(job, "Corporate");
            String division = getMetadataValue(job, "Division");

            PredefinedCorporate corporate = toCorporate(corporateValue);
            Set<String> categories = division.isBlank() ? new HashSet<>() : Set.of(division);

            results.add(RecruitmentNoticeDto.builder()
                    .jobOfferTitle(title)
                    .url(url)
                    .corporateCodes(Set.of(corporate.name()))
                    .categories(categories)
                    .build());
        }

        return results;
    }

    private String getMetadataValue(JsonNode job, String metadataName) {
        JsonNode metadata = job.get("metadata");
        if (metadata == null) return "";
        for (JsonNode meta : metadata) {
            if (metadataName.equals(meta.path("name").asText())) {
                JsonNode value = meta.get("value");
                return (value == null || value.isNull()) ? "" : value.asText();
            }
        }
        return "";
    }

    private PredefinedCorporate toCorporate(String corporateValue) {
        if ("당근페이".equals(corporateValue)) {
            return PredefinedCorporate.KARROT_PAY;
        }
        return PredefinedCorporate.KARROT_MARKET;
    }
}

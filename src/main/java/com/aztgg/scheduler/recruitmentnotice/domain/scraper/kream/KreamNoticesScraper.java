package com.aztgg.scheduler.recruitmentnotice.domain.scraper.kream;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KreamNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String API_URL = "https://recruit.kreamcorp.com/rcrt/loadJobList.do";
    private static final int PAGE_SIZE = 10;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        List<RecruitmentNoticeDto> results = new ArrayList<>();

        for (int firstIndex = 0; ; firstIndex += PAGE_SIZE) {
            String json = Jsoup.connect(API_URL + "?firstIndex=" + firstIndex)
                    .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
                    .ignoreContentType(true)
                    .timeout(10000)
                    .execute()
                    .body();

            JsonNode root = OBJECT_MAPPER.readTree(json);
            JsonNode list = root.get("list");

            if (list == null || list.isEmpty()) break;

            for (JsonNode item : list) {
                String title = item.get("annoSubject").asText();
                String url = item.has("jobDetailLink")
                        ? item.get("jobDetailLink").asText()
                        : "https://recruit.kreamcorp.com/rcrt/view.do?annoId=" + item.get("annoId").asText();

                String companyCd = item.has("sysCompanyCdNm") ? item.get("sysCompanyCdNm").asText() : "";
                Set<String> corporateCodes = Set.of(
                        "KREAM Pay".equalsIgnoreCase(companyCd)
                                ? PredefinedCorporate.KREAM_PAY.name()
                                : PredefinedCorporate.KREAM.name()
                );

                results.add(RecruitmentNoticeDto.builder()
                        .jobOfferTitle(title)
                        .url(url)
                        .corporateCodes(corporateCodes)
                        .build());
            }

            if (list.size() < PAGE_SIZE) break;
        }

        return results;
    }
}

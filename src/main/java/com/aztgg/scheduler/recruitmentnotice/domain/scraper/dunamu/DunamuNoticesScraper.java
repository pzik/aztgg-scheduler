package com.aztgg.scheduler.recruitmentnotice.domain.scraper.dunamu;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DunamuNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String LIST_URL = "https://dunamu.com/careers/jobs";
    private static final String CAREERS_BASE = "https://careers.dunamu.com";

    // dunamu.com/careers/jobs 페이지 내부 인라인 JSON에서 공고 항목 추출
    private static final Pattern ENTRY_PATTERN = Pattern.compile(
            "\"categoryDisplayNameEn\":\"([^\"]+)\",\"categoryKind\":\"([^\"]+)\"," +
            "\"title\":\"([^\"]+)\",\"subject\":\"([^\"]*)\",\"summary\":\"([^\"]+)\""
    );

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        String html = Jsoup.connect(LIST_URL)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
                .timeout(15000)
                .execute()
                .body();

        // (title, url) 기준 중복 제거
        Set<String> seen = new LinkedHashSet<>();
        List<RecruitmentNoticeDto> results = new ArrayList<>();

        Matcher matcher = ENTRY_PATTERN.matcher(html);
        while (matcher.find()) {
            String category = matcher.group(1);
            String kind     = matcher.group(2);
            String title    = matcher.group(3);
            String subject  = matcher.group(4);
            String summary  = matcher.group(5);

            if (!"LINK".equals(kind)) continue;

            // careers.dunamu.com URL 결정: subject 우선, 없으면 summary
            String url = subject.startsWith(CAREERS_BASE) ? subject : summary;
            if (!url.startsWith(CAREERS_BASE)) continue;

            String dedupeKey = title + "|" + url;
            if (!seen.add(dedupeKey)) continue;

            results.add(RecruitmentNoticeDto.builder()
                    .jobOfferTitle(title)
                    .url(url)
                    .categories(Set.of(category))
                    .corporateCodes(Set.of(PredefinedCorporate.DUNAMU.name()))
                    .build());
        }

        return results;
    }
}

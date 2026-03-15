package com.aztgg.scheduler.recruitmentnotice.domain.scraper.dunamu;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DunamuNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String DUNAMU_CAREERS_URL = "https://dunamu.com/careers/jobs";
    private static final String CAREERS_DETAIL_PREFIX = "https://careers.dunamu.com/careers/";
    private static final Pattern JOB_GROUP_PATTERN = Pattern.compile("\"jobGroupCode\":\"([^\"]+)\"");

    private static final Map<String, String> JOB_GROUP_MAP = Map.ofEntries(
            Map.entry("T_ENGINEERING", "Engineering"),
            Map.entry("T_BUSINESS", "Business"),
            Map.entry("T_COMMUNICATION", "Communication"),
            Map.entry("T_MARKETING", "Marketing"),
            Map.entry("T_PEOPLE", "People"),
            Map.entry("T_SECURITY", "Security"),
            Map.entry("T_LEGAL", "Legal"),
            Map.entry("T_TALENT_POOL", "Talent Pool")
    );

    @Override
    public List<RecruitmentNoticeDto> scrap() {
        try {
            AppLogger.infoLog("Starting to scrape Dunamu job list from: {}", DUNAMU_CAREERS_URL);

            // 1. dunamu.com/careers/jobs 에서 careers.dunamu.com 상세 링크 추출
            Document listPage = Jsoup.connect(DUNAMU_CAREERS_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();

            Set<String> detailUrls = listPage.select("a[href^=" + CAREERS_DETAIL_PREFIX + "]").stream()
                    .map(a -> a.attr("href").trim())
                    .filter(url -> url.contains("/detail/"))
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            if (detailUrls.isEmpty()) {
                AppLogger.warnLog("No careers.dunamu.com detail links found on dunamu.com/careers/jobs");
                return Collections.emptyList();
            }

            AppLogger.infoLog("Found {} detail URLs from dunamu.com/careers/jobs", detailUrls.size());

            // 2. 상세 페이지 하나에서 dehydrated query data로 모든 공고 정보 추출
            String firstDetailUrl = detailUrls.iterator().next();
            Document detailPage = Jsoup.connect(firstDetailUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(30000)
                    .get();

            String fullHtml = detailPage.html();

            // positionId -> 공고 정보 매핑
            Map<String, JobInfo> jobInfoMap = extractJobInfoFromDehydratedData(fullHtml);
            AppLogger.infoLog("Extracted {} job infos from dehydrated data", jobInfoMap.size());

            // 3. 각 URL에서 positionId 추출하여 매핑
            List<RecruitmentNoticeDto> allNotices = new ArrayList<>();
            for (String detailUrl : detailUrls) {
                String positionId = detailUrl.substring(detailUrl.lastIndexOf("/") + 1);
                JobInfo info = jobInfoMap.get(positionId);

                if (info != null) {
                    String category = JOB_GROUP_MAP.getOrDefault(info.jobGroupCode, info.jobGroupCode);
                    allNotices.add(RecruitmentNoticeDto.builder()
                            .jobOfferTitle(info.jobNoticeName)
                            .url(detailUrl)
                            .categories(Set.of(category))
                            .corporateCodes(Set.of(PredefinedCorporate.DUNAMU.name()))
                            .startAt(null)
                            .endAt(null)
                            .build());
                } else {
                    // dehydrated data에 없으면 상세 페이지 직접 파싱
                    scrapNoticeDetail(detailUrl).ifPresent(allNotices::add);
                }
            }

            AppLogger.infoLog("Successfully scraped {} Dunamu job notices in total.", allNotices.size());
            return allNotices;
        } catch (Exception e) {
            AppLogger.errorLog(String.format("Error scraping Dunamu job list: %s", e.getMessage()), e);
            return Collections.emptyList();
        }
    }

    private Map<String, JobInfo> extractJobInfoFromDehydratedData(String html) {
        Map<String, JobInfo> map = new HashMap<>();
        // 각 query entry에서 positionId, jobNoticeName, jobGroupCode 추출
        // 패턴: "positionId":557,"jobNoticeName":"...", "jobGroupCode":"T_..."
        Pattern entryPattern = Pattern.compile(
                "\"jobNoticeId\":\\d+,\"positionId\":(\\d+),\"jobNoticeName\":\"([^\"]+)\",\"jobNoticeContent\":[^,]*,\"jobGroupCode\":\"([^\"]+)\""
        );
        Matcher matcher = entryPattern.matcher(html);
        while (matcher.find()) {
            String positionId = matcher.group(1);
            String name = matcher.group(2);
            String groupCode = matcher.group(3);
            map.put(positionId, new JobInfo(name, groupCode));
        }
        return map;
    }

    private Optional<RecruitmentNoticeDto> scrapNoticeDetail(String detailUrl) {
        try {
            AppLogger.infoLog("Scraping Dunamu job detail (fallback) from: {}", detailUrl);
            Document doc = Jsoup.connect(detailUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(30000)
                    .get();

            // title from og:title meta tag (format: "제목 | 두나무")
            Element ogTitle = doc.selectFirst("meta[property=og:title]");
            String title = null;
            if (ogTitle != null) {
                title = ogTitle.attr("content").replaceAll("\\s*\\|\\s*두나무$", "").trim();
            }
            if (title == null || title.isEmpty()) {
                return Optional.empty();
            }

            // category from dehydrated data in this page
            String html = doc.html();
            String category = "Engineering";
            Matcher groupMatcher = JOB_GROUP_PATTERN.matcher(html);
            if (groupMatcher.find()) {
                category = JOB_GROUP_MAP.getOrDefault(groupMatcher.group(1), groupMatcher.group(1));
            }

            return Optional.of(RecruitmentNoticeDto.builder()
                    .jobOfferTitle(title)
                    .url(detailUrl)
                    .categories(Set.of(category))
                    .corporateCodes(Set.of(PredefinedCorporate.DUNAMU.name()))
                    .startAt(null)
                    .endAt(null)
                    .build());
        } catch (Exception e) {
            AppLogger.errorLog(String.format("Failed to scrape detail page: %s. Error: %s", detailUrl, e.getMessage()), e);
            return Optional.empty();
        }
    }

    private record JobInfo(String jobNoticeName, String jobGroupCode) {}
}

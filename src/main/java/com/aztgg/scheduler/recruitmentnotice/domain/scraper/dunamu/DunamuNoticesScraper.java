package com.aztgg.scheduler.recruitmentnotice.domain.scraper.dunamu;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class DunamuNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String DUNAMU_CAREERS_URL = "https://dunamu.com/careers/jobs";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<RecruitmentNoticeDto> scrap() {
        List<RecruitmentNoticeDto> allNotices = new ArrayList<>();
        try {
            AppLogger.infoLog("Starting to scrape Dunamu job list from: {}", DUNAMU_CAREERS_URL);
            Document document = Jsoup.connect(DUNAMU_CAREERS_URL)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                    .get();
            Element nextDataScript = document.selectFirst("script#__NEXT_DATA__");
            if (nextDataScript == null) {
                AppLogger.warnLog("Could not find __NEXT_DATA__ script in Dunamu careers page.");
                return Collections.emptyList();
            }
            String jsonData = nextDataScript.html();
            NextData listData = objectMapper.readValue(jsonData, NextData.class);

            if (listData.props() != null && listData.props().pageProps() != null &&
                listData.props().pageProps().articles() != null && listData.props().pageProps().articles().content() != null) {

                for (ArticleSummary summary : listData.props().pageProps().articles().content()) {
                    if (summary.id() != null && summary.id() != 0) {
                        String detailUrl = DUNAMU_CAREERS_URL + "/" + summary.id();
                        scrapNoticeDetail(detailUrl).ifPresent(allNotices::add);
                    }
                }
            }
            AppLogger.infoLog("Successfully scraped {} Dunamu job notices in total.", allNotices.size());
            return allNotices;
        } catch (Exception e) {
            String errorMessage = String.format("An error occurred while scraping Dunamu job list page: %s. Error: %s", DUNAMU_CAREERS_URL, e.getMessage());
            AppLogger.errorLog(errorMessage, e);
            return Collections.emptyList();
        }
    }

    private Optional<RecruitmentNoticeDto> scrapNoticeDetail(String detailUrl) {
        try {
            AppLogger.infoLog("Scraping Dunamu job detail from: {}", detailUrl);
            Document document = Jsoup.connect(detailUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .timeout(30000)
                    .get();
            Element nextDataScript = document.selectFirst("script#__NEXT_DATA__");
            if (nextDataScript == null) {
                AppLogger.warnLog("Could not find __NEXT_DATA__ script on detail page: {}", detailUrl);
                return Optional.empty();
            }
            String jsonData = nextDataScript.html();
            NextData detailData = objectMapper.readValue(jsonData, NextData.class);

            ArticleDetail article = detailData.props().pageProps().article().article();

            if (article == null || article.category() == null || !"ARTICLE".equals(article.category().kind())) {
                AppLogger.infoLog("Skipping non-article page: {}", detailUrl);
                return Optional.empty();
            }

            String title = article.title();
            if (title == null || title.isEmpty()) {
                return Optional.empty();
            }

            Set<String> categories = new HashSet<>();
            if (article.category().displayName() != null && !article.category().displayName().isEmpty()) {
                categories.add(article.category().displayName());
            }

            LocalDateTime startAt = parseDateTime(article.createdAt());
            LocalDateTime endAt = parseDateTime(article.updatedAt());

            return Optional.of(RecruitmentNoticeDto.builder()
                    .jobOfferTitle(title)
                    .url(detailUrl)
                    .categories(categories)
                    .corporateCodes(Set.of(PredefinedCorporate.DUNAMU.name()))
                    .startAt(startAt)
                    .endAt(endAt)
                    .build());
        } catch (Exception e) {
            String errorMessage = String.format("Failed to scrape Dunamu job detail page: %s. Error: %s", detailUrl, e.getMessage());
            AppLogger.errorLog(errorMessage, e);
            return Optional.empty();
        }
    }

    private LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            AppLogger.warnLog("Failed to parse datetime string: '{}'. It is not in ISO_LOCAL_DATE_TIME format.", dateTimeString);
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NextData(Props props) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Props(PageProps pageProps) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PageProps(ArticleList articles, ArticleContainer article) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArticleList(List<ArticleSummary> content) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArticleSummary(Long id) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArticleContainer(ArticleDetail article) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArticleDetail(
            String title,
            String createdAt,
            String updatedAt,
            Category category
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record Category(String kind, String displayName) {}
}

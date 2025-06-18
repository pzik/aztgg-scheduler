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

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DunamuNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String DUNAMU_CAREERS_URL = "https://dunamu.com/careers/jobs";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        try {
            AppLogger.infoLog("Starting to scrape Dunamu job postings from HTML: {}", DUNAMU_CAREERS_URL);
            
            Document document = Jsoup.connect(DUNAMU_CAREERS_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            // __NEXT_DATA__ 스크립트에서 JSON 데이터 추출
            Element nextDataScript = document.selectFirst("script#__NEXT_DATA__");
            if (nextDataScript == null) {
                AppLogger.warnLog("Could not find __NEXT_DATA__ script in Dunamu careers page");
                return Collections.emptyList();
            }
            
            String jsonData = nextDataScript.html();
            DunamuNextDataDto nextData = objectMapper.readValue(jsonData, DunamuNextDataDto.class);
            
            if (nextData.props() == null || nextData.props().pageProps() == null || 
                nextData.props().pageProps().articles() == null || 
                nextData.props().pageProps().articles().content() == null) {
                AppLogger.warnLog("No job postings found in Dunamu __NEXT_DATA__");
                return Collections.emptyList();
            }
            
            List<RecruitmentNoticeDto> notices = nextData.props().pageProps().articles().content().stream()
                    .filter(article -> "ARTICLE".equals(article.categoryKind())) // 공지사항 제외, 실제 채용공고만
                    .map(this::convertToRecruitmentNotice)
                    .collect(Collectors.toList());
            
            AppLogger.infoLog("Successfully scraped {} Dunamu job notices from HTML", notices.size());
            return notices;
            
        } catch (Exception e) {
            AppLogger.errorLog("Failed to scrape Dunamu job postings from HTML. Error: {}", e);
            return Collections.emptyList();
        }
    }

    private RecruitmentNoticeDto convertToRecruitmentNotice(DunamuArticleDto article) {
        Set<String> categories = new HashSet<>();
        if (article.categoryDisplayName() != null && !article.categoryDisplayName().isEmpty()) {
            categories.add(article.categoryDisplayName());
        }
        
        String jobUrl = "https://dunamu.com/careers/jobs/" + article.id();
        
        LocalDateTime startAt = parseDateTime(article.createdAt());
        LocalDateTime endAt = parseDateTime(article.updatedAt());
        
        return RecruitmentNoticeDto.builder()
                .jobOfferTitle(article.title())
                .url(jobUrl)
                .categories(categories)
                .corporateCodes(Set.of(PredefinedCorporate.DUNAMU.name()))
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
    
    private LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        try {
            // ISO 8601 형식 ("2025-06-02T19:29:09") 파싱
            return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            AppLogger.warnLog("Failed to parse datetime: {}, error: {}", dateTimeString, e.getMessage());
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record DunamuNextDataDto(PropsDto props) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PropsDto(PagePropsDto pageProps) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record PagePropsDto(ArticlesDto articles) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ArticlesDto(List<DunamuArticleDto> content) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record DunamuArticleDto(
            Long id,
            String title,
            String summary,
            String categoryDisplayName,
            String categoryKind,
            String createdAt,
            String updatedAt
    ) {}
}
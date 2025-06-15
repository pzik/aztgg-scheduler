package com.aztgg.scheduler.recruitmentnotice.domain.scraper.sendbird;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SendbirdNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String SENDBIRD_CAREERS_URL = "https://sendbird.com/ko/careers";
    private final RestClient sendbirdCareersPublicRestClient;

    public SendbirdNoticesScraper(RestClient sendbirdCareersPublicRestClient) {
        this.sendbirdCareersPublicRestClient = sendbirdCareersPublicRestClient;
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        try {
            log.info("Starting to scrape Sendbird job postings from Greenhouse API");
            
            SendbirdJobsApiResponseDto response = sendbirdCareersPublicRestClient.get()
                    .uri("/jobs?content=false")
                    .retrieve()
                    .body(SendbirdJobsApiResponseDto.class);
            
            if (response == null || response.jobs() == null) {
                log.warn("No job postings found in Sendbird Greenhouse API response");
                return Collections.emptyList();
            }
            
            // 웹사이트에서 카테고리 정보 크롤링
            Map<String, String> jobCategories = scrapeJobCategories();
            
            List<RecruitmentNoticeDto> notices = response.jobs().stream()
                    .map(job -> convertToRecruitmentNotice(job, jobCategories))
                    .collect(Collectors.toList());
            
            log.info("Successfully scraped {} Sendbird job notices from Greenhouse API", notices.size());
            return notices;
            
        } catch (Exception e) {
            log.error("Failed to fetch jobs from Sendbird Greenhouse API. Error: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private Map<String, String> scrapeJobCategories() {
        try {
            Document document = Jsoup.connect(SENDBIRD_CAREERS_URL)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            Map<String, String> categories = new HashMap<>();
            
            // 채용 공고 목록에서 각 공고의 카테고리 정보 추출
            document.select("div.job-wrap").forEach(jobElement -> {
                Element titleElement = jobElement.selectFirst("div.title");
                Element departmentElement = jobElement.selectFirst("div.department");
                
                if (titleElement != null && departmentElement != null) {
                    String title = titleElement.text().trim();
                    String category = departmentElement.text().trim();
                    if (!title.isEmpty() && !category.isEmpty()) {
                        categories.put(title, category);
                    }
                }
            });
            
            log.info("Successfully scraped {} job categories from Sendbird website", categories.size());
            return categories;
            
        } catch (Exception e) {
            log.warn("Failed to scrape job categories from Sendbird website: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    private RecruitmentNoticeDto convertToRecruitmentNotice(SendbirdJobDto job, Map<String, String> jobCategories) {
        LocalDateTime startAt = job.firstPublished() != null ? 
            job.firstPublished().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() : null;
        LocalDateTime endAt = job.updatedAt() != null ? 
            job.updatedAt().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() : null;
            
        Set<String> categories = new HashSet<>();
        
        // 웹사이트에서 크롤링한 카테고리 정보 사용
        String category = jobCategories.get(job.title());
        if (category != null && !category.isEmpty()) {
            categories.add(category);
        }
        
        String jobUrl = "https://sendbird.com/ko/careers?gh_jid=" + job.id();
            
        return RecruitmentNoticeDto.builder()
                .jobOfferTitle(job.title())
                .url(jobUrl)
                .categories(categories)
                .corporateCodes(Set.of(PredefinedCorporate.SENDBIRD.name()))
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }

    private record SendbirdJobsApiResponseDto(List<SendbirdJobDto> jobs, MetaDto meta) {}

    private record MetaDto(Integer total) {}

    private record SendbirdJobDto(
            Long id,
            String title,
            @JsonProperty("absolute_url") String absoluteUrl,
            @JsonProperty("company_name") String companyName,
            LocationDto location,
            @JsonProperty("first_published") OffsetDateTime firstPublished,
            @JsonProperty("updated_at") OffsetDateTime updatedAt
    ) {}

    private record LocationDto(String name) {}
} 
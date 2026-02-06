package com.aztgg.scheduler.recruitmentnotice.domain.scraper.moloco;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class MolocoNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String MOLOCO_CAREERS_URL = "https://www.moloco.com/ko/open-positions";
    private final RestClient molocoCareersPublicRestClient;

    public MolocoNoticesScraper(RestClient molocoCareersPublicRestClient) {
        this.molocoCareersPublicRestClient = molocoCareersPublicRestClient;
    }

    /**
     * 몰로코 공고 스크랩
     * @return 몰로코 공고 스크랩 목록
     * */
    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        try {
            AppLogger.infoLog("Starting to scrape Moloco job postings");
            
            // 먼저 카테고리 정보를 크롤링
            Map<String, String> jobCategories = scrapeJobCategories();
            AppLogger.infoLog("Scraped categories: {}", jobCategories);
            
            // API에서 job 정보를 가져옴
            MolocoJobsApiResponseDto response = molocoCareersPublicRestClient.get()
                    .uri("/jobs?content=false")
                    .retrieve()
                    .body(MolocoJobsApiResponseDto.class);

            if (response == null || response.jobs() == null) {
                AppLogger.warnLog("No job data received from Moloco API");
                return new ArrayList<>();
            }

            List<RecruitmentNoticeDto> jobs = response.jobs().stream()
                    .map(job -> convertToRecruitmentNotice(job, jobCategories))
                    .collect(Collectors.toList());

            AppLogger.infoLog("Successfully scraped {} jobs from Moloco", jobs.size());
            return jobs;

        } catch (Exception e) {
            AppLogger.errorLog("Failed to fetch jobs from Moloco API. Error: {}", e);
            return new ArrayList<>();
        }
    }

    private Map<String, String> scrapeJobCategories() {
        WebDriver driver = null;
        try {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("--disable-gpu");
            options.addArguments("--remote-allow-origins=*");
            
            driver = new ChromeDriver(options);
            driver.get(MOLOCO_CAREERS_URL);
            
            // 페이지가 로드될 때까지 대기
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(d -> d.findElement(By.cssSelector("div[class*='job-departments-section']")));
            
            // 잠시 대기하여 동적 콘텐츠가 모두 로드되도록 함
            Thread.sleep(2000);
            
            Map<String, String> categories = new HashMap<>();
            
            // 각 부서 섹션의 카테고리 정보 추출
            List<WebElement> sections = driver.findElements(By.cssSelector("div[class*='job-departments-section']"));
            for (WebElement section : sections) {
                try {
                    WebElement categoryElement = section.findElement(By.cssSelector("h4"));
                    if (categoryElement != null) {
                        String category = categoryElement.getText().trim();
                        if (!category.isEmpty()) {
                            // 해당 섹션의 모든 job 링크를 찾아서 카테고리와 매핑
                            List<WebElement> jobLinks = section.findElements(By.cssSelector("a[href*='/jobs/']"));
                            for (WebElement jobLink : jobLinks) {
                                String jobUrl = jobLink.getAttribute("href");
                                if (jobUrl != null && !jobUrl.isEmpty()) {
                                    categories.put(jobUrl, category);
                                    AppLogger.debugLog("Mapped job URL {} to category {}", jobUrl, category);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    AppLogger.warnLog("Failed to extract category from section: {}", e.getMessage());
                }
            }
            
            AppLogger.infoLog("Successfully scraped {} job categories from Moloco website", categories.size());
            return categories;
            
        } catch (Exception e) {
            AppLogger.warnLog("Failed to scrape job categories from Moloco website: {}", e.getMessage());
            return Collections.emptyMap();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

    private RecruitmentNoticeDto convertToRecruitmentNotice(MolocoJobDto job, Map<String, String> jobCategories) {
        LocalDateTime startAt = job.firstPublished() != null ? 
            job.firstPublished().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() : null;
        LocalDateTime endAt = job.updatedAt() != null ? 
            job.updatedAt().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime() : null;
            
        Set<String> categories = new HashSet<>();
        
        // job URL을 키로 사용하여 카테고리 매핑
        String category = jobCategories.get(job.absoluteUrl());
        if (category != null && !category.isEmpty()) {
            categories.add(category);
        }
            
        return RecruitmentNoticeDto.builder()
                .jobOfferTitle(job.title())
                .url(job.absoluteUrl())
                .categories(categories)
                .corporateCodes(Set.of(PredefinedCorporate.MOLOCO.name()))
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }

    private record MolocoJobsApiResponseDto(List<MolocoJobDto> jobs, MetaDto meta) {}

    private record MetaDto(Integer total) {}

    private record MolocoJobDto(
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
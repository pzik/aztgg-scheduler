package com.aztgg.scheduler.recruitmentnotice.domain.scraper.nexon;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

@Slf4j
public class NexonNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String MAIN_URL = "https://careers.nexon.com/recruit";
    private static final Map<String, String> jobCategories = Map.of(
            "6", "Game Programming",
            "3", "Game Design",
            "9", "Game Art",
            "2", "Business",
            "1", "Tech & Analytics",
            "4", "Development Management",
            "7", "Game Service & QA",
            "5", "Design & Multimedia",
            "8", "Management Support",
            "10", "Etc"
    );

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        List<RecruitmentNoticeDto> result = new ArrayList<>();
        Future<List<RecruitmentNoticeDto>> future = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        WebDriver webDriver = null;
        try {
            final WebDriver finalWebDriver = webDriver = createWebDriver();
            for (var job : jobCategories.entrySet()) {
                webDriver.get(MAIN_URL + "?jobCategories=" + job.getKey());
                Thread.sleep(4000);

                future = executor.submit(() -> scrapJobCategory(finalWebDriver, job.getValue()));

                // 한 잡 카테고리 스크랩 당 타임아웃 시간 ⏰ 300초 제한
                List<RecruitmentNoticeDto> notices = future.get(300, TimeUnit.SECONDS);
                result.addAll(notices);
            }
        } catch (Exception e) {
            log.error("internal error", e);
            if (future != null) {
                future.cancel(true); // 현재 작업 취소
            }
            return new ArrayList<>(); // 한번이라도 실패하면 return empty
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }

        // graceful shutdown
        executor.shutdown();
        try {
            if (executor.awaitTermination(60, TimeUnit.SECONDS)) {
            } else {
                log.warn(LocalTime.now() + " some jobs are not terminated, shutdown now");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        return result;
    }

    private WebDriver createWebDriver() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--lang=ko");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
        chromeOptions.addArguments("--disable-gpu");

        chromeOptions.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        return driver;
    }

    private List<RecruitmentNoticeDto> scrapJobCategory(WebDriver webDriver, String jobValue) throws InterruptedException {
        List<RecruitmentNoticeDto> result = new ArrayList<>();
        // 무한스크롤 끝까지 내리기
        infiniteScroll(webDriver);

        List<WebElement> noticeItems = webDriver.findElements(By.cssSelector("ul.notice-list > li a"));
        for (var noticeItem : noticeItems) {
            // url
            String url = noticeItem.getDomProperty("href");
            if (!StringUtils.hasText(url)) {
                continue;
            }
            url = url.split("\\?")[0];

            // title
            String offerTitle = noticeItem.findElement(By.cssSelector("h4")).getText();

            // corp
            WebElement companyElem = noticeItem.findElement(By.cssSelector("span.text-company"));
            String corp = companyElem.getText();

            RecruitmentNoticeDto noticeDto = RecruitmentNoticeDto.builder()
                    .url(url)
                    .jobOfferTitle(offerTitle)
                    .corporateCodes(Set.of(PredefinedCorporate.fromId(corp).name()))
                    .categories(Set.of(jobValue))
                    .build();
            result.add(noticeDto);
        }
        return result;
    }

    // 천천히 내려가며 무한스크롤
    private void infiniteScroll(WebDriver webDriver) throws InterruptedException {
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        long lastCurHeight = -10;

        while (true) {
            js.executeScript("window.scrollBy(0, 50);");
            Thread.sleep(150);
            long curHeight = (long) js.executeScript("return window.pageYOffset;");
            if (curHeight <= lastCurHeight) {
                break;
            }
            lastCurHeight = curHeight;
        }
    }
}

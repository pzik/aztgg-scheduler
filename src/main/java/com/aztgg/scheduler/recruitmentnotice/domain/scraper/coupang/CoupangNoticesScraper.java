package com.aztgg.scheduler.recruitmentnotice.domain.scraper.coupang;

import com.aztgg.scheduler.global.util.HashUtils;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CoupangNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    /**
     * 쿠팡 공고 스크랩 (지역 : 서울, 대한민국)
     * @return 쿠팡 공고 스크랩 목록
     * */
    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        int page = 1;
        boolean hasJob = true;
        String BASE_URL = "https://www.coupang.jobs/kr/jobs/";
        List<RecruitmentNoticeDto> jobListResponseAll = new ArrayList<>();

        while (hasJob) {
            String url = String.format("%s?page=%d&location=Seoul,%%20South%%20Korea&location=South%%20Korea&pagesize=20#results", BASE_URL ,page);
            Document doc = getDocument(url);

            Elements jobCards = doc.select("div.card.card-job");
            if (jobCards.isEmpty()) {
                hasJob = false;
            }

            List<RecruitmentNoticeDto> jobListResponse = new ArrayList<>();
            for (Element card : jobCards) {
                Element jobAction = card.selectFirst("div.card-job-actions.js-job");

                String annoId = jobAction.attr("data-id");
                String jobTitle = jobAction.attr("data-jobtitle");
                String annoUrl = BASE_URL + annoId;

                Document anno = getDocument(annoUrl);
                try {
                    Thread.sleep(300);
                } catch (Exception e) {
                    log.error("unexpected exception", e);
                    throw new IllegalStateException();
                }
                String updateTime = anno.selectFirst("time[datetime]").attr("datetime");

                RecruitmentNoticeDto dto = RecruitmentNoticeDto.builder()
                        .jobOfferTitle(jobTitle)
                        .hash(HashUtils.encrypt(jobTitle+annoId+updateTime))
                        .url(annoUrl)
                        .build();

                jobListResponse.add(dto);
            }

            jobListResponseAll.addAll(jobListResponse);
            page++;

            try {
                Thread.sleep(600);
            } catch (Exception e) {
                log.error("unexpected exception", e);
                throw new IllegalStateException();
            }
        }

        return jobListResponseAll;
    }

    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7")
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "ko,en-US;q=0.9,en;q=0.8,ko-KR;q=0.7")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "none")
                .header("Sec-Fetch-User", "?1")
                .header("Sec-CH-UA", "\"Google Chrome\";v=\"135\", \"Not-A.Brand\";v=\"8\", \"Chromium\";v=\"135\"")
                .header("Sec-CH-UA-Mobile", "?0")
                .header("Sec-CH-UA-Platform", "\"macOS\"")
                .header("Cookie", "ph_cookiePref=N; UMB_SESSION=CfDJ8M8dnfYreA1GpSGqsJMV%2Fnf6eWkSn0WU5j6Sm6lojDcJgkl6AG6JaCFgpeUNA6yztATOZxaDYUt9A4FO1eCat1AjIz5iUAU6evssrhvlXGJ378KiIiGB2oCRh6GonPrtoC%2BGMANW%2Bs9fMjegdId1SeLCDl45%2BmsVBzdIVJ2yisFn")
                .timeout(10000)
                .get();
    }
}

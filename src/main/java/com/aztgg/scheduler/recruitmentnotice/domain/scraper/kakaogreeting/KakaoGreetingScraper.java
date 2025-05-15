package com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakaogreeting;

import com.aztgg.scheduler.company.domain.Corporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 그리팅을 사용하는 카카오 계열사 스크래퍼
 */
public class KakaoGreetingScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private final Set<Corporate> kakaoGreetingCorp = Set.of(Corporate.KAKAO_MOBILITY,
            Corporate.KAKAO_PAY, Corporate.KAKAO_ENTERPRISE, Corporate.KAKAO_GAMES);

    public KakaoGreetingScraper() {
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        List<RecruitmentNoticeDto> jobListResponseAll = new ArrayList<>();

        for (var corp : kakaoGreetingCorp) {
            String documentHost;
            String detailBaseUrl;
            if (corp.equals(Corporate.KAKAO_MOBILITY)) {
                documentHost = "https://kakaomobility.career.greetinghr.com/guide";
                detailBaseUrl = "https://kakaomobility.career.greetinghr.com";
            } else if (corp.equals(Corporate.KAKAO_PAY)) {
                documentHost = "https://kakaopay.career.greetinghr.com/main";
                detailBaseUrl = "https://kakaopay.career.greetinghr.com";
            } else if (corp.equals(Corporate.KAKAO_ENTERPRISE)) {
                documentHost = "https://careers.kakaoent.com/job";
                detailBaseUrl = "https://careers.kakaoent.com";
            } else if (corp.equals(Corporate.KAKAO_GAMES)) {
                documentHost = "https://recruit.kakaogames.com/joinjuskr";
                detailBaseUrl = "https://recruit.kakaogames.com/";
            } else {
                throw new IllegalArgumentException("유효하지 않은 카카오 그리팅 사용하는 계열사");
            }

            /**
             * 계열사별 스크랩 시작
             */
            jobListResponseAll.addAll(getNotices(corp, documentHost, detailBaseUrl));
        }

        return jobListResponseAll;
    }

    private List<RecruitmentNoticeDto> getNotices(Corporate corp, String documentHost, String detailBaseUrl) throws IOException {
        List<RecruitmentNoticeDto> result = new ArrayList<>();
        Document doc = getDocument(documentHost);
        Elements jobCards = doc.select("a[href^=/o]");

        for (var card : jobCards) {
            String detailId = card.attr("href");
            String detailUrl = detailBaseUrl + detailId;

            Elements divs = card.select("li > div > div");
            Element titleElement = divs.get(0);
            Element metaElement = divs.get(1);

            Elements metaElementItems = metaElement.select("span");
            Element categoryElement = metaElementItems.get(2);

            String title = titleElement.text();
            String category = categoryElement.text();

            RecruitmentNoticeDto noticeDto = RecruitmentNoticeDto.builder()
                    .corporateCodes(Set.of(corp.name()))
                    .jobOfferTitle(title)
                    .categories(Set.of(category))
                    .url(detailUrl)
                    .build();

            result.add(noticeDto);
        }
        return result;
    }

    private static Document getDocument(String url) throws IOException {
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

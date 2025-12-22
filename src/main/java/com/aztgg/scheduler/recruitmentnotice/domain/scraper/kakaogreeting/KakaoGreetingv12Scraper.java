package com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakaogreeting;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 그리팅을 1.2 버전 사용하는 카카오 계열사 스크래퍼
 */
public class KakaoGreetingv12Scraper implements Scraper<List<RecruitmentNoticeDto>> {

    private final RestClient baseRestClient;
    private final Set<PredefinedCorporate> kakaoGreetingCorp = Set.of(PredefinedCorporate.KAKAO_ENTERPRISE);

    public KakaoGreetingv12Scraper(RestClient baseRestClient) {
        this.baseRestClient = baseRestClient;
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        List<RecruitmentNoticeDto> jobListResponseAll = new ArrayList<>();

        for (var corp : kakaoGreetingCorp) {
            String documentHost;
            String detailBaseUrl;
            String positionApi;
            if (corp.equals(PredefinedCorporate.KAKAO_ENTERPRISE)) {
                documentHost = "https://careers.kakaoenterprise.com/ko/job";
                detailBaseUrl = "https://careers.kakaoenterprise.com";
                positionApi = "https://api.greetinghr.com/ats/v1.2/career/workspaces/12556/job-positions/filter";
            } else {
                throw new IllegalArgumentException("유효하지 않은 카카오 그리팅 사용하는 계열사");
            }

            /**
             * 계열사별 스크랩 시작
             */
            jobListResponseAll.addAll(getNotices(corp, documentHost, detailBaseUrl, positionApi));
        }

        return jobListResponseAll;
    }

    private List<RecruitmentNoticeDto> getNotices(PredefinedCorporate corp, String documentHost, String detailBaseUrl, String positionApi) throws IOException {
        // api 통해 직군 추출
        ApiResponseDto occupationRes = baseRestClient.get()
                .uri(positionApi)
                .retrieve()
                .body(ApiResponseDto.class);
        Set<String> categories = occupationRes.data().occupations().stream()
                .map(OccupationDto::occupation)
                .collect(Collectors.toSet());

        List<RecruitmentNoticeDto> result = new ArrayList<>();
        Document doc = getDocument(documentHost);
        Elements jobCards = doc.select("a[href~=.*/o/.*]");

        for (var card : jobCards) {
            String detailId = card.attr("href");
            String detailUrl = detailBaseUrl + detailId;

            Elements divs = card.select("li > div > div");
            Element titleElement = divs.get(0);
            Element metaElement = divs.get(1);

            Elements metaElementItems = metaElement.select("span");

            String title = titleElement.text();
            String category = "무관";

            // 카테고리 선출
            for (var item : metaElementItems) {
                if (StringUtils.hasText(item.text()) && categories.contains(item.text())) {
                    category = item.text();
                    break;
                }
            }

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

    private record ApiResponseDto(DataDto data) {

    }

    private record DataDto(List<OccupationDto> occupations) {

    }

    private record OccupationDto(String occupation) {

    }
}


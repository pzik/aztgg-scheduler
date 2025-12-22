package com.aztgg.scheduler.recruitmentnotice.domain.scraper.krafton;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KraftonNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String DOC_HOST = "https://www.krafton.com/careers/jobs/?search_list_cnt=999&search_office=Pangyo,Seoul,Remote";
    private static final String DETAIL_URL = "https://www.krafton.com";

    public KraftonNoticesScraper() {
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        Document doc = getDocument(DOC_HOST);
        Elements jobCards = doc.select("ul.RecruitList-list div.RecruitItem");

        List<RecruitmentNoticeDto> result = new ArrayList<>();
        for (var card : jobCards) {
            String detailUrl = getDetailUrl(card);
            String title = getTitle(card);
            String corpId = getCorpId(card);
            String getCategory = getCategory(card);

            PredefinedCorporate corporate = PredefinedCorporate.fromId(corpId);
            result.add(RecruitmentNoticeDto.builder()
                    .url(detailUrl)
                    .jobOfferTitle(title)
                    .corporateCodes(Set.of(corporate.name()))
                    .categories(Set.of(getCategory))
                    .build());
        }
        return result;
    }

    private String getDetailUrl(Element card) {
        String detailUrl = DETAIL_URL;
        Element link = card.selectFirst("a"); // 첫 번째 a 태그 선택
        if (link != null) {
            String href = link.attr("href");
            detailUrl += href;
        } else {
            AppLogger.warnLog("krafon, invalid detail url format");
        }
        return detailUrl;
    }

    private String getTitle(Element card) {
        Element titleWrapper = card.selectFirst("div.RecruitItemTitle");
        String title = "";
        if (titleWrapper != null) {
            Element firstSpan = titleWrapper.selectFirst("span");
            if (firstSpan != null) {
                title = firstSpan.text();
            } else {
                AppLogger.warnLog("krafon, invalid title format");
            }
        } else {
            AppLogger.warnLog("krafon, invalid title format");
        }
        return title;
    }

    private String getCorpId(Element card) {
        String corpId = "";
        Element studioSpan = card.selectFirst("span.RecruitItemMeta-studio");
        if (studioSpan != null) {
            corpId = studioSpan.attr("data-str");
        } else {
            AppLogger.warnLog("krafon, invalid corp format");
        }
        return corpId;
    }

    private String getCategory(Element card) {
        String category = "";
        Element categorySpan = card.selectFirst("span.RecruitItemMetaCategory-text");
        if (categorySpan != null) {
            category = categorySpan.text();
        } else {
            AppLogger.warnLog("krafon, invalid category format");
        }
        return category;
    }

    private static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .sslSocketFactory(socketFactory())
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

    private static SSLSocketFactory socketFactory() {
        // 크래프톤 SSL 인증서 에러 대응
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory result = sslContext.getSocketFactory();

            return result;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }
}

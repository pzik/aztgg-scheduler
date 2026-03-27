package com.aztgg.scheduler.recruitmentnotice.domain.scraper.kream;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KreamNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String BASE_URL = "https://recruit.kreamcorp.com";
    private static final String LIST_URL = BASE_URL + "/rcrt/list.do";
    private static final Pattern SHARE_ID_PATTERN = Pattern.compile("share\\('\\w+','(\\d+)'");

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        Document doc = getDocument(LIST_URL);
        Elements jobCards = doc.select("ul > li");
        List<RecruitmentNoticeDto> results = new ArrayList<>();

        for (Element card : jobCards) {
            Element titleEl = card.selectFirst("h4");
            if (titleEl == null) continue;

            String title = titleEl.text().trim();
            if (title.isEmpty()) continue;

            // share 함수에서 공고 ID 추출
            String annoId = extractAnnoId(card);
            if (annoId == null) continue;

            String url = BASE_URL + "/rcrt/view.do?rcrtId=" + annoId;

            // 법인 구분 (제목에서 추출)
            Set<String> corporateCodes = Set.of(
                    title.contains("KREAM Pay") ? PredefinedCorporate.KREAM_PAY.name() : PredefinedCorporate.KREAM.name()
            );

            results.add(RecruitmentNoticeDto.builder()
                    .jobOfferTitle(title)
                    .url(url)
                    .corporateCodes(corporateCodes)
                    .build());
        }

        return results;
    }

    private String extractAnnoId(Element card) {
        Elements shareLinks = card.select("a[href*=share]");
        for (Element link : shareLinks) {
            Matcher matcher = SHARE_ID_PATTERN.matcher(link.attr("href"));
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "ko,en-US;q=0.9,en;q=0.8")
                .timeout(10000)
                .get();
    }
}

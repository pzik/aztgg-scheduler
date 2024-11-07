package com.aztgg.scheduler.recruitmentnotice.domain.scraper;

import com.aztgg.scheduler.global.crawler.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

// 디자이너 채용은 따로 없는듯
public class WoowahanNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String DETAIL_URL = "https://career.woowahan.com/recruitment/%s/detail";

    private final RestClient woowahanCareersPublicRestClient;

    public WoowahanNoticesScraper(RestClient woowahanCareersPublicRestClient) {
        this.woowahanCareersPublicRestClient = woowahanCareersPublicRestClient;
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        WoowahanCareersApiResponseDto responseDto = woowahanCareersPublicRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/w1/recruits")
                        .queryParam("category", "jobGroupCodes:BA005001")
                        .queryParam("recruitCampaignSeq", 0)
                        .queryParam("jobGroupCodes", "BA005001")
                        .queryParam("page", 0)
                        .queryParam("size", "9999")
                        .queryParam("sort", "updateDate,desc")
                        .build())
                .retrieve()
                .body(WoowahanCareersApiResponseDto.class);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return responseDto.data().list().stream()
                .map(item -> {
                    String url = String.format(DETAIL_URL, item.recruitNumber());
                    var startFormatter = formatter.parse(item.recruitOpenDate, java.time.LocalDateTime::from);
                    var endFormatter = formatter.parse(item.recruitEndDate, java.time.LocalDateTime::from);

                    OffsetDateTime startOffsetDateTime = OffsetDateTime.of(startFormatter, ZoneOffset.ofHours(9));
                    OffsetDateTime endOffsetDateTime = OffsetDateTime.of(endFormatter, ZoneOffset.ofHours(9));
                    return RecruitmentNoticeDto.builder()
                            .url(url)
                            .jobOfferTitle(item.recruitName)
                            .startAt(startOffsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                            .endAt(endOffsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime())
                            .build();
                }).toList();
    }

    private record WoowahanCareersApiResponseDto(String code,
                                                 DataDto data) {

    }

    private record DataDto(List<JobItemDto> list) {

    }

    private record JobItemDto(String recruitOpenDate,
                              String recruitEndDate,
                              String recruitName,
                              String recruitNumber) {

    }
}

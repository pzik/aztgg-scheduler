package com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakaobank;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class KakaoBankNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private final RestClient kakaoBankCareersPublicRestClient;

    public KakaoBankNoticesScraper(RestClient kakaoBankCareersPublicRestClient) {
        this.kakaoBankCareersPublicRestClient = kakaoBankCareersPublicRestClient;
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        int page = 1;  // 시작은 1
        Integer totalPage = null;
        List<RecruitmentNoticeDto> result = new ArrayList<>();

        do {
            KakaoBankApiResponseDto response = response(page);
            result.addAll(getResult(response));
            if (totalPage == null) {
                totalPage = response.paging().totalPages();
            }

            page++;
            try {
                Thread.sleep(1000); // 1초 쉬고
            } catch (Exception e) {
                AppLogger.errorLog("unexpected exception", e);
                throw new IllegalStateException();
            }
        } while (page <= totalPage);
        return result;
    }

    private KakaoBankApiResponseDto response(int page) {
        return kakaoBankCareersPublicRestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/recruits")
                        .build())
                .body(Map.of("pageNumber", page, "pageSize", 100))
                .retrieve()
                .body(KakaoBankApiResponseDto.class);
    }

    private List<RecruitmentNoticeDto> getResult(KakaoBankApiResponseDto response) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        return response.list().stream()
                .map(item -> {
                    var startFormatter = formatter.parse(item.receiveStartDatetime(), java.time.LocalDateTime::from);
                    var endFormatter = formatter.parse(item.receiveEndDatetime(), java.time.LocalDateTime::from);
                    if (endFormatter.isBefore(now)) {
                        return null;
                    }

                    return RecruitmentNoticeDto.builder()
                            .url("https://" + item.recruitNoticeUrl())
                            .jobOfferTitle(item.recruitNoticeName())
                            .categories(Set.of(item.recruitClassName()))
                            .corporateCodes(Set.of(PredefinedCorporate.KAKAO_BANK.name()))
                            .startAt(startFormatter)
                            .endAt(endFormatter)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private record KakaoBankApiResponseDto(PagingDto paging, List<ListItemDto> list) {

    }

    private record PagingDto(int totalPages) {

    }

    private record ListItemDto(long recruitNoticeSn,
                               String recruitNoticeName,
                               String recruitNoticeUrl,
                               String recruitClassName, // 직무
                               String receiveStartDatetime,
                               String receiveEndDatetime) {

    }
}

package com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakaobank;

import com.aztgg.scheduler.global.crawler.Scraper;
import com.aztgg.scheduler.global.util.HashUtils;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
                log.error("unexpected exception", e);
                return result;
            }
        } while (page <= totalPage);
        return result;
    }

    private KakaoBankApiResponseDto response(int page) {
        return kakaoBankCareersPublicRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/user/recruit")
                        .queryParam("pageNumber", page)
                        .queryParam("pageSize", 100) // 100개씩
                        .build())
                .retrieve()
                .body(KakaoBankApiResponseDto.class);
    }

    private List<RecruitmentNoticeDto> getResult(KakaoBankApiResponseDto response) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return response.list().stream()
                .map(item -> {
                    var startFormatter = formatter.parse(item.receiveStartDatetime(), java.time.LocalDateTime::from);
                    var endFormatter = formatter.parse(item.receiveEndDatetime(), java.time.LocalDateTime::from);

                    return RecruitmentNoticeDto.builder()
                            .hash(HashUtils.encrypt(String.valueOf(item.hashCode())))
                            .url("https://" + item.recruitNoticeUrl())
                            .jobOfferTitle(item.recruitNoticeName())
                            .categories(Set.of(item.recruitClassName()))
                            .startAt(startFormatter)
                            .endAt(endFormatter)
                            .build();
                }).collect(Collectors.toList());
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

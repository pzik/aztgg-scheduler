package com.aztgg.scheduler.recruitmentnotice.domain.scraper;

import com.aztgg.scheduler.global.crawler.Scraper;
import com.aztgg.scheduler.global.util.HashUtils;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class KakaoNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String JOB_DETAIL_URL = "https://careers.kakao.com/jobs";

    private final RestClient kakaoCareersPublicRestClient;
    private final KakaoPartType partType;
    private final int page;

    public KakaoNoticesScraper(RestClient kakaoCareersPublicRestClient, int page, KakaoPartType partType) {
        this.kakaoCareersPublicRestClient = kakaoCareersPublicRestClient;
        this.partType = partType;
        this.page = page;

        if (page < 1) {
            throw new IndexOutOfBoundsException("page 는 1 이상이여야 합니다.");
        }
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        KakaoCareersApiResponseDto response = kakaoCareersPublicRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/job-list")
                        .queryParam("skillSet", "")
                        .queryParam("part", partType.getCode())
                        .queryParam("company", "ALL")
                        .queryParam("keyword", "")
                        .queryParam("employeeType", "")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .body(KakaoCareersApiResponseDto.class);

        // dto로 변환 후 응답
        return response.jobList.stream()
                .map(item -> RecruitmentNoticeDto.builder()
                        .jobOfferTitle(item.jobOfferTitle)
                        .url(JOB_DETAIL_URL + "/" + item.realId)
                        .hash(HashUtils.encrypt(item.realId))
                        .startAt(item.regDate)
                        .endAt(item.endDate)
                        .build())
                .toList();
    }

    private record KakaoCareersApiResponseDto(List<KakaoJobDto> jobList,
                                              List<KakaoJobTypeCountDto> jobTypeCountDtoList,
                                              Integer totalJobCount,
                                              Integer totalPage) {

    }

    // 우리에게 필요한 필드만 나열
    private record KakaoJobDto(String realId, // hash 값으로 사용하자
                               boolean privateFlag,
                               boolean pinFlag,
                               boolean closeFlag,
                               String jobOfferTitle,
                               LocalDateTime uptDate,
                               LocalDateTime regDate,
                               LocalDateTime endDate,
                               int recruitCount // 0명이면 채용 인원 지정없음 의미
                               ) {

    }

    private record KakaoJobTypeCountDto(String jobType,
                                        Integer jobCount) {

    }
}

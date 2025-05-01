package com.aztgg.scheduler.recruitmentnotice.domain.scraper.kakao;

import com.aztgg.scheduler.global.crawler.Scraper;
import com.aztgg.scheduler.global.util.HashUtils;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 카카오는 계열사별로 채용 사이트가 별도로 있음, 우리는 "카카오"만 추출한다.
 * 계열사는 별도로 추출해야함
 */
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
                        .queryParam("part", partType.getCode())
                        .queryParam("skillSet", "") // part 내 스킬셋 전체 조회
                        .queryParam("company", "KAKAO")
                        .queryParam("keyword", "")
                        .queryParam("employeeType", "")
                        .queryParam("page", page)
                        .build())
                .retrieve()
                .body(KakaoCareersApiResponseDto.class);

        // dto로 변환 후 응답
        return response.jobList().stream()
                .map(item -> {
                    Set<String> categories = new HashSet<>();
                    if (Objects.nonNull(item.skillSetList())) {
                        categories = item.skillSetList().stream()
                                .map(a -> a.skillSetType)
                                .collect(Collectors.toSet());
                    }

                    return RecruitmentNoticeDto.builder()
                            .jobOfferTitle(item.jobOfferTitle)
                            .url(JOB_DETAIL_URL + "/" + item.realId)
                            .hash(HashUtils.encrypt(String.valueOf(item.hashCode())))
                            .categories(categories)
                            .startAt(item.regDate)
                            .endAt(item.endDate)
                            .build();
                })
                .toList();
    }

    private record KakaoCareersApiResponseDto(List<KakaoJobDto> jobList,
                                              List<KakaoJobTypeCountDto> jobTypeCountDtoList,
                                              Integer totalJobCount,
                                              Integer totalPage) {

    }

    // 우리에게 필요한 필드만 나열
    private record KakaoJobDto(String realId,
                               boolean privateFlag,
                               boolean pinFlag,
                               boolean closeFlag,
                               String jobOfferTitle,
                               LocalDateTime uptDate,
                               LocalDateTime regDate,
                               LocalDateTime endDate,
                               List<SkillSetDto> skillSetList,
                               int recruitCount // 0명이면 채용 인원 지정없음 의미
                               ) {

    }

    private record SkillSetDto(Long id, String skillSetType) {

    }

    private record KakaoJobTypeCountDto(String jobType,
                                        Integer jobCount) {

    }
}

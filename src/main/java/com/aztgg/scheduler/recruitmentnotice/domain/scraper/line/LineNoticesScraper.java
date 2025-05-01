package com.aztgg.scheduler.recruitmentnotice.domain.scraper.line;

import com.aztgg.scheduler.global.crawler.Scraper;
import com.aztgg.scheduler.global.util.HashUtils;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


public class LineNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String DETAIL_URL = "https://careers.linecorp.com/ko/jobs/%s";
    private final RestClient lineCareersPublicRestClient;

    public LineNoticesScraper(RestClient lineCareersPublicRestClient) {
        this.lineCareersPublicRestClient = lineCareersPublicRestClient;
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        LineCareersApiResponseDto res = lineCareersPublicRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/page-data/ko/jobs/page-data.json")
                        .build())
                .retrieve()
                .body(LineCareersApiResponseDto.class);

        return res.result().data().allStrapiJobs().edges().stream()
                .map(edge -> {
                    if (Objects.isNull(edge.node())) {
                        return null;
                    }
                    StrapiJobEdgeNodeDto node = edge.node();
                    if (!node.hasValidCity()) {
                        return null;
                    }

                    // 공개되지 않은 공고
                    if (!node.publish()) {
                        return null;
                    }

                    Set<String> categories = new HashSet<>();
                    if (Objects.nonNull(node.jobUnits)) {
                        categories = node.jobUnits().stream()
                                .map(StrapiJobEdgeNodeJobUnitDto::name)
                                .collect(Collectors.toSet());
                    }

                    LocalDateTime startAt = node.startDate().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
                    LocalDateTime endAt = Objects.isNull(node.endDate()) ? null : node.endDate().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
                    return RecruitmentNoticeDto.builder()
                            .hash(HashUtils.encrypt(String.valueOf(node.hashCode())))
                            .url(String.format(DETAIL_URL, node.strapiId()))
                            .jobOfferTitle(node.title())
                            .categories(categories)
                            .startAt(startAt)
                            .endAt(endAt)
                            .build();
                }).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private record LineCareersApiResponseDto(ResultDto result) {

    }

    private record ResultDto(ResultDataDto data) {

    }

    private record ResultDataDto(StrapiJobDto allStrapiJobs) {

    }

    private record StrapiJobDto(int totalCount, List<StrapiJobEdgeDto> edges) {

    }

    private record StrapiJobEdgeDto(StrapiJobEdgeNodeDto node) {

    }

    private record StrapiJobEdgeNodeDto(boolean publish,
                                        Long strapiId,
                                        @JsonProperty("end_date") OffsetDateTime endDate, // null일 수 있음
                                        @JsonProperty("start_date") OffsetDateTime startDate,
                                        String title,
                                        List<StrapiJobEdgeNodeCityDto> cities, // 유효한 도시만
                                        @JsonProperty("job_unit") List<StrapiJobEdgeNodeJobUnitDto> jobUnits // 직무카테고리로 쓰자
                                        ) {

        // 유효한 도시 내 공고인 경우
        public boolean hasValidCity() {
            return cities.stream()
                    .anyMatch(StrapiJobEdgeNodeCityDto::isValidCity);
        }
    }

    private record StrapiJobEdgeNodeCityDto(String name) {

        // 분당, 과천, 서울꺼만
        public boolean isValidCity() {
            if (Objects.isNull(name)) {
                return false;
            }
            return name.equals("Seoul") || name.equals("Gwacheon") || name.equals("Bundang");
        }
    }

    private record StrapiJobEdgeNodeJobUnitDto(String name) {

    }
}

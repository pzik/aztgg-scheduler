package com.aztgg.scheduler.recruitmentnotice.domain.scraper.line;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jsoup.Jsoup;

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

    private static final String PAGE_DATA_URL = "https://careers.linecorp.com/page-data/ko/jobs/page-data.json";
    private static final String DETAIL_URL = "https://careers.linecorp.com/ko/jobs/%s";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        String json = Jsoup.connect(PAGE_DATA_URL)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
                .ignoreContentType(true)
                .timeout(15000)
                .execute()
                .body();

        LineCareersApiResponseDto res = OBJECT_MAPPER.readValue(json, LineCareersApiResponseDto.class);

        return res.result().data().allStrapiJobs().edges().stream()
                .map(edge -> {
                    if (Objects.isNull(edge.node())) {
                        return null;
                    }
                    StrapiJobEdgeNodeDto node = edge.node();

                    if (!node.publish()) {
                        return null;
                    }
                    if (!node.hasValidCity()) {
                        return null;
                    }

                    Set<String> categories = new HashSet<>();
                    if (node.jobUnits() != null) {
                        categories = node.jobUnits().stream()
                                .map(StrapiJobEdgeNodeJobUnitDto::name)
                                .collect(Collectors.toSet());
                    }

                    Set<String> corporates = new HashSet<>();
                    if (node.companies() != null) {
                        corporates = node.companies().stream()
                                .map(c -> PredefinedCorporate.fromId(c.name()))
                                .map(Enum::name)
                                .collect(Collectors.toSet());
                    }

                    LocalDateTime startAt = node.startDate() != null
                            ? node.startDate().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
                            : null;

                    return RecruitmentNoticeDto.builder()
                            .url(String.format(DETAIL_URL, node.strapiId()))
                            .jobOfferTitle(node.title())
                            .categories(categories)
                            .corporateCodes(corporates)
                            .startAt(startAt)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record LineCareersApiResponseDto(ResultDto result) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ResultDto(ResultDataDto data) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ResultDataDto(StrapiJobDto allStrapiJobs) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StrapiJobDto(int totalCount, List<StrapiJobEdgeDto> edges) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StrapiJobEdgeDto(StrapiJobEdgeNodeDto node) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StrapiJobEdgeNodeDto(
            boolean publish,
            Long strapiId,
            @JsonProperty("end_date") OffsetDateTime endDate,
            @JsonProperty("start_date") OffsetDateTime startDate,
            String title,
            List<StrapiJobEdgeNodeCityDto> cities,
            List<StrapiJobEdgeNodeCompanyDto> companies,
            @JsonProperty("job_unit") List<StrapiJobEdgeNodeJobUnitDto> jobUnits
    ) {
        public boolean hasValidCity() {
            if (cities == null || cities.isEmpty()) return false;
            return cities.stream().anyMatch(StrapiJobEdgeNodeCityDto::isValidCity);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StrapiJobEdgeNodeCityDto(String name) {
        public boolean isValidCity() {
            return "Seoul".equals(name) || "Gwacheon".equals(name) || "Bundang".equals(name);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StrapiJobEdgeNodeCompanyDto(String name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record StrapiJobEdgeNodeJobUnitDto(String name) {}
}

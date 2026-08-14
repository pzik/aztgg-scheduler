package com.aztgg.scheduler.recruitmentnotice.domain.scraper.sendbird;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SendbirdNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private static final String API_URL = "https://boards-api.greenhouse.io/v1/boards/sendbird/jobs?content=false";
    private static final String JOB_URL_FORMAT = "https://delight.ai/ko/careers?gh_jid=%s#jobs";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        String json = Jsoup.connect(API_URL)
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36")
                .ignoreContentType(true)
                .timeout(15000)
                .execute()
                .body();

        SendbirdJobsApiResponseDto response = OBJECT_MAPPER.readValue(json, SendbirdJobsApiResponseDto.class);
        if (response.jobs() == null) {
            return Collections.emptyList();
        }

        return response.jobs().stream()
                .map(job -> {
                    Set<String> categories = extractCategories(job);
                    return RecruitmentNoticeDto.builder()
                            .jobOfferTitle(job.title().strip())
                            .url(String.format(JOB_URL_FORMAT, job.id()))
                            .categories(categories)
                            .corporateCodes(Set.of(PredefinedCorporate.SENDBIRD.name()))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private Set<String> extractCategories(SendbirdJobDto job) {
        if (job.metadata() == null) return new HashSet<>();
        return job.metadata().stream()
                .filter(m -> "External department name".equals(m.name()))
                .filter(m -> m.value() != null)
                .flatMap(m -> m.value().stream())
                .collect(Collectors.toSet());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SendbirdJobsApiResponseDto(List<SendbirdJobDto> jobs) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record SendbirdJobDto(
            Long id,
            String title,
            @JsonProperty("absolute_url") String absoluteUrl,
            List<MetadataDto> metadata
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record MetadataDto(
            String name,
            List<String> value,
            @JsonProperty("value_type") String valueType
    ) {}
}

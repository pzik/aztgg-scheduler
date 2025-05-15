package com.aztgg.scheduler.recruitmentnotice.domain.scraper.toss;

import com.aztgg.scheduler.company.domain.Corporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 토스는 모든 카테고리의 공지를 하나의 API로 제공
 */
public class TossTotalNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private final RestClient tossCareersPublicRestClient;

    public TossTotalNoticesScraper(RestClient tossCareersPublicRestClient) {
        this.tossCareersPublicRestClient = tossCareersPublicRestClient;
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        TossCareersApiResponseDto responseDto = tossCareersPublicRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v3/ipd-eggnog/career/job-groups")
                        .build())
                .retrieve()
                .body(TossCareersApiResponseDto.class);

        return responseDto.success().stream()
                .map(item -> {
                    PrimaryJobDto primaryJob = item.primaryJob();
                    String url = primaryJob.absoluteUrl();
                    OffsetDateTime startOffsetDateTime = primaryJob.firstPublished();
                    RecruitmentNoticeDto.RecruitmentNoticeDtoBuilder recruitmentNoticeDtoBuilder = RecruitmentNoticeDto.builder()
                            .url(url)
                            .jobOfferTitle(primaryJob.title())
                            .startAt(startOffsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());

                    Optional<JobMetadata> optJobCategory = primaryJob.getJobCategoryMetadata();
                    if (optJobCategory.isEmpty() || Objects.isNull(optJobCategory.get().value)) { // 카테고리가 있어야함
                        return null;
                    }

                    // 공고 대상 법인 추출
                    Set<String> corporates = new HashSet<>();
                    if (Objects.nonNull(item.jobs())) {
                        corporates = item.jobs().stream()
                                .map(JobDto::getJobCorpMetadata)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .filter(a -> Objects.nonNull(a.value()))
                                .map(a -> String.valueOf(a.value()))
                                .map(Corporate::fromId)
                                .map(Corporate::name)
                                .collect(Collectors.toSet());
                    }

                    // 카테고리 추출
                    JobMetadata categoryMeta = optJobCategory.get();
                    return recruitmentNoticeDtoBuilder
                            .categories(Set.of(categoryMeta.value.toString()))
                            .corporateCodes(corporates)
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record TossCareersApiResponseDto(List<JobItemDto> success) {

    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record JobItemDto(Long id,
                              PrimaryJobDto primaryJob, // 계열사들 공통 공고를 묶는 최상단공고
                              List<JobDto> jobs // 각 계열사 or 계열사 내 포지션별 공고
    ) {

    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record PrimaryJobDto(Long id, String absoluteUrl, String title, OffsetDateTime firstPublished, List<JobMetadata> metadata) {

        public Optional<JobMetadata> getJobCategoryMetadata() {
            if (CollectionUtils.isEmpty(metadata)) {
                return Optional.empty();
            }
            return metadata.stream()
                    .filter(item -> item.id().equals(4168924003L))
                    .findFirst();
        }
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record JobDto(List<JobMetadata> metadata) {

        public Optional<JobMetadata> getJobCorpMetadata() {
            if (CollectionUtils.isEmpty(metadata)) {
                return Optional.empty();
            }
            return metadata.stream()
                    .filter(item -> item.id().equals(4169410003L))
                    .findFirst();
        }
    }

    /**
     * id 가 4169410003 인 경우 value 가 공고 대상 자회사임 (토스, 토스뱅크 등)
     * id 가 4168924003 인 경우 job category 를  의미 (개발자 - Engineering (Product), 디자이너 - Design)
     */
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record JobMetadata(Long id, Object value) {

    }
}

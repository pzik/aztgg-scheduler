package com.aztgg.scheduler.recruitmentnotice.domain.scraper;

import com.aztgg.scheduler.global.crawler.Scraper;
import com.aztgg.scheduler.global.util.HashUtils;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
                            .hash(HashUtils.encrypt(primaryJob.id().toString()))
                            .jobOfferTitle(primaryJob.title())
                            .startAt(startOffsetDateTime.atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime());

                    Optional<JobMetadata> optJobCategory = primaryJob.getJobCategoryMetadata();
                    if (optJobCategory.isEmpty()) {
                        return null;
                    }
                    // 개발자, 디자이너 공고만 올림
                    JobMetadata category = optJobCategory.get();
                    if (category.isDeveloper() || category.isDesigner()) {
                        return recruitmentNoticeDtoBuilder.build();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record TossCareersApiResponseDto(List<JobItemDto> success) {

    }

    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record JobItemDto(Long id, PrimaryJobDto primaryJob) {

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

    /**
     * id 가 4169410003 인 경우 value 가 공고 대상 자회사임 (토스, 토스뱅크 등)
     * id 가 4168924003 인 경우 job category 를  의미 (개발자 - Engineering (Product), 디자이너 - Design)
     */
    @JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
    private record JobMetadata(Long id, Object value) {

        public boolean isDeveloper() {
            if (Objects.isNull(value) || !id.equals(4168924003L)) {
                return false;
            }
            return ((String) value).contains("Engineering");
        }

        public boolean isDesigner() {
            if (Objects.isNull(value) || !id.equals(4168924003L)) {
                return false;
            }
            return value.equals("Design");
        }
    }
}

package com.aztgg.scheduler.recruitmentnotice.domain.scraper.nexon;

import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class NexonNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private final RestClient nexonCareersPublicRestClient;

    private static final String DETAIL_URL = "https://careers.nexon.com/recruit/%s";
    private static final Map<String, String> jobCategories = Map.of(
            "6", "Game Programming",
            "3", "Game Design",
            "9", "Game Art",
            "2", "Business",
            "1", "Tech & Analytics",
            "4", "Development Management",
            "7", "Game Service & QA",
            "5", "Design & Multimedia",
            "8", "Management Support",
            "10", "Etc"
    );

    public NexonNoticesScraper(RestClient nexonCareersPublicRestClient) {
        this.nexonCareersPublicRestClient = nexonCareersPublicRestClient;
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        List<RecruitmentNoticeDto> result = new ArrayList<>();
        for (var jobCategory : jobCategories.entrySet()) {
            Map<String, Object> requestBody = Map.of(
                    "page", "1",
                    "size", "9000",
                    "jobCategories", Set.of(jobCategory.getKey()));

            try {
                NexonCareersApiResponseDto responseDto = nexonCareersPublicRestClient.post()
                        .uri("/career/v1/open/job-posts")
//                    .contentType(MediaType.APPLICATION_JSON)
                        .body(requestBody)
                        .retrieve()
                        .body(NexonCareersApiResponseDto.class);

                responseDto.list()
                        .forEach(item -> {
                            String url = String.format(DETAIL_URL, item.jobPostNo());
                            LocalDate localDate = LocalDate.parse(item.startDate(), formatter);
                            var startFormat = localDate.atStartOfDay(ZoneId.of("Asia/Seoul"));

                            String corp = PredefinedCorporate.fromId(item.corpName()).name();
                            result.add(RecruitmentNoticeDto.builder()
                                    .url(url)
                                    .jobOfferTitle(item.title())
                                    .corporateCodes(Set.of(corp))
                                    .categories(Set.of(jobCategory.getValue()))
                                    .startAt(LocalDateTime.ofInstant(startFormat.toInstant(), ZoneOffset.UTC))
                                    .build());
                        });
            } catch (Exception e) {
                // 한번이라도 실패하면 빈 배열 반환해서 diff check 일어나지 않게끔
                log.error("internal error", e);
                return new ArrayList<>();
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                log.error("internal error", e);
            }
        }

        return result;
    }

    private record NexonCareersApiResponseDto(List<CareersJobDto> list) {

    }

    private record CareersJobDto(String corpName,
                                 Long jobPostNo,
                                 String title,
                                 String startDate) {

    }
}

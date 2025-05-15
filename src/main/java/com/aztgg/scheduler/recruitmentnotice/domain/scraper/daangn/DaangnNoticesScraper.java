package com.aztgg.scheduler.recruitmentnotice.domain.scraper.daangn;

import com.aztgg.scheduler.company.domain.Corporate;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DaangnNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private final RestClient daangnCareersPublicRestClient;

    public DaangnNoticesScraper(RestClient daangnCareersPublicRestClient) {
        this.daangnCareersPublicRestClient = daangnCareersPublicRestClient;
    }

    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        DaangnCareersApiResponseDto res = daangnCareersPublicRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/page-data/jobs/page-data.json")
                        .build())
                .retrieve()
                .body(DaangnCareersApiResponseDto.class);

        // 당근 직무 목록 해시맵화
        Map<String, String> depMap = new HashMap<>();
        for (var i : res.result().data().allJobDepartment().nodes()) {
            depMap.put(i.id(), i.name());
        }

        return res.result().data().allDepartmentFilteredJobPost().nodes().stream()
                .map(node -> {
                    Set<String> categories = node.departments().stream()
                            .map(a -> depMap.get(a.id()))
                            .collect(Collectors.toSet());
                    Set<String> corporateCodes = Set.of(Corporate.fromId(node.corporate()).name());

                    return RecruitmentNoticeDto.builder()
                            .jobOfferTitle(node.title())
                            .url(node.absoluteUrl())
                            .categories(categories)
                            .corporateCodes(corporateCodes)
                            .build();
                }).collect(Collectors.toList());
    }

    private record DaangnCareersApiResponseDto(ResultDto result) {

    }

    private record ResultDto(ResultDataDto data) {

    }

    private record ResultDataDto(DepartmentFilteredJobPostDto allDepartmentFilteredJobPost, AllJobDepartmentDto allJobDepartment) {

    }

    private record DepartmentFilteredJobPostDto(List<DepartmentFilteredJobPostNodeDto> nodes) {

    }

    private record DepartmentFilteredJobPostNodeDto(String title,
                                                    String corporate, // 법인
                                                    String absoluteUrl,
                                                    List<NodeDepartmentDto> departments // 직무
    ) {

    }

    private record NodeDepartmentDto(String id, String name) {

    }



    private record AllJobDepartmentDto(List<AllJobDepartmentNodeDto> nodes) {

    }

    private record AllJobDepartmentNodeDto(String id, String name) {

    }
}

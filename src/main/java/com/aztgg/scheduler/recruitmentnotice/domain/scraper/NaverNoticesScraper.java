package com.aztgg.scheduler.recruitmentnotice.domain.scraper;

import com.aztgg.scheduler.global.crawler.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.List;

public class NaverNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private final RestClient.Builder commonRestClient;
    private final String BASE_URL = "https://recruit.navercorp.com/rcrt/view.do?annoId=";

    public NaverNoticesScraper(RestClient.Builder commonRestClient) {
        this.commonRestClient = commonRestClient;
    }

// TODO utc 변환

    /**
     * 네이버 공고 스크랩
     * @return 네이버 공고 스크랩 목록
     * */
    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        RestClient client = commonRestClient
                .baseUrl("https://recruit.navercorp.com/rcrt/loadJobList.do")
                .build();

        String responseBody = client.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("subJobCdData", NaverSubJobCodeData.FRONTEND.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.ANDROID.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.IOS.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.BACKEND.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.AIML.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.DATAENGINEERING.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.EMBEDDEDSW.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.GRAPHICS.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.SWCOMMON.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.HARDWARE.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.INFRAENGINEERING.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.DATACENTERENGINEERING.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.SECURITY.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.TECHSTAFF.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.QA.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.TECHCOMMON.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.PRODUCTDESIGN.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.DVCBD.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.INTERACTIVEDESIGN.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.PEIP.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.CONTENTWRITING.getCode())
                        .queryParam("subJobCdData", NaverSubJobCodeData.DESIGNCOMMON.getCode())
                        .build())
                .retrieve()
                .body(String.class);

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(responseBody, NaverCareersApiResponseDto.class).list.stream()
                .map(item -> RecruitmentNoticeDto.builder()
                        .jobOfferTitle(item.annoSubject)
                        .url(BASE_URL+item.annoId)
                        .startAt(item.staYmdTime)
                        .endAt(item.endYmdTime)
                        .build())
                .toList();
    }

    private record NaverCareersApiResponseDto(
            String result,
            List<NaverJobDto> list,
            Integer totalSize){
    }

    // todo 접근 방법?
    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NaverJobDto(String annoId, // 공고 id
                               String annoSubject, // 공고 제목
                               String staYmdTime, // 채용 시작일
                               String endYmdTime, // 채용 마감일
                               String entTypeCd, // 경력 구분 코드
                               String entTypeCdNm, // 경력 구분
                               String reqTypeCd, // 채용 방식 코드
                               String reqTypeCdNm, // 채용 방식
                               String stateCd, // 공고 상태 코드
                               String stateCdNm, // 공고 상태
                               String annoKeyword, // 분야
                               String empTypeCdNm, // 근로 형태
                               String classCdNm // 직무 분야
                               ){
    }
}

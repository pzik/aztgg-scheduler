package com.aztgg.scheduler.recruitmentnotice.domain.scraper.naver;

import com.aztgg.scheduler.global.util.HashUtils;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.Scraper;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class NaverNoticesScraper implements Scraper<List<RecruitmentNoticeDto>> {

    private final RestClient naverCareersPublicRestClient;
    private final ObjectMapper objectMapper = new ObjectMapper();


    public NaverNoticesScraper(RestClient naverCareersPublicRestClient) {
        this.naverCareersPublicRestClient = naverCareersPublicRestClient;
    }

    /**
     * 네이버 공고 스크랩
     * @return 네이버 공고 스크랩 목록
     * */
    @Override
    public List<RecruitmentNoticeDto> scrap() throws IOException {
        int firstIndex = 0;
        int totalSize = Integer.MAX_VALUE;
        List<RecruitmentNoticeDto> jobListResponseAll = new ArrayList<>();

        do{
            String uri = "/rcrt/loadJobList.do?annoId=&sw=&subJobCdArr=&sysCompanyCdArr=&empTypeCdArr=&entTypeCdArr=&workAreaCdArr=&firstIndex=" + firstIndex;

            String response = naverCareersPublicRestClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);

            List<RecruitmentNoticeDto> jobListResponse = objectMapper.readValue(response,
                    NaverCareersApiResponseDto.class).list.stream()
                    .map(item -> RecruitmentNoticeDto.builder()
                            .jobOfferTitle(item.annoSubject)
                            .url(item.jobDetailLink)
                            .categories(new HashSet<>(Set.of(item.subJobCdNm)))
                            .hash(HashUtils.encrypt(item.annoSubject+item.staYmdTime+item.endYmdTime))
                            .startAt(LocalDateTime.parse(item.staYmdTime, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")))
                            .endAt(LocalDateTime.parse(item.endYmdTime, DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss")))
                            .build()).toList();

            jobListResponseAll.addAll(jobListResponse);


            if(totalSize == Integer.MAX_VALUE){
                JsonNode rootNode = objectMapper.readTree(response);
                totalSize = rootNode.path("totalSize").asInt();
            }
            firstIndex++;

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("unexpected exception", e);
                throw new IllegalStateException();
            }

        }while(firstIndex < totalSize);

        return jobListResponseAll.stream()
                .filter(distinctByKey(RecruitmentNoticeDto::getHash))
                .collect(Collectors.toList());

    }

    private record NaverCareersApiResponseDto(
            String result,
            List<NaverJobDto> list,
            Integer totalSize
    ){
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record NaverJobDto(String annoId, // 공고 id
                               String jobDetailLink, // 공고 url
                               String annoSubject, // 공고 제목
                               String staYmdTime, // 채용 시작일
                               String endYmdTime, // 채용 마감일
                               String subJobCdNm // 카테고리
                               ){
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}

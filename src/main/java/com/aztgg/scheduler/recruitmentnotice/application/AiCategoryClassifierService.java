package com.aztgg.scheduler.recruitmentnotice.application;

import com.aztgg.scheduler.company.domain.StandardCategory;
import com.aztgg.scheduler.recruitmentnotice.application.dto.CategoryClassifyRequestDto;
import com.aztgg.scheduler.recruitmentnotice.application.dto.CategoryClassifyResponseDto;
import com.aztgg.scheduler.recruitmentnotice.domain.categoryclassifier.CategoryClassifierSystemInstruction;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNotice;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.errors.ClientException;
import com.google.genai.types.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AiCategoryClassifierService {

    private final RecruitmentNoticeRepository recruitmentNoticeRepository;
    private final Client geminiClient;
    private final ObjectMapper objectMapper;

    private static final String GEMINI_MODEL = "gemini-2.0-flash";

    /**
     * 카테고리 분류 수행
     */
    public void classifyingNoticeCategories() {
        List<RecruitmentNotice> recruitmentNotices = recruitmentNoticeRepository.findAllStandardCategoryIsNullOrEmpty();
        if (CollectionUtils.isEmpty(recruitmentNotices)) {
            return;
        }
        log.info("new recruitmentNotice found " + recruitmentNotices.size() + ", classifying category start");

        // Sets the system instruction in the config.
        CategoryClassifierSystemInstruction categoryClassifierSystemInstruction = new CategoryClassifierSystemInstruction(objectMapper, StandardCategory.values());
        Content systemInstruction = Content.fromParts(Part.fromText(categoryClassifierSystemInstruction.print()));

        GenerateContentConfig config = GenerateContentConfig.builder()
                .responseMimeType("application/json")
                .candidateCount(1)
                .systemInstruction(systemInstruction)
                .build();

        // 10개씩 잘라 질의
        int batch = 10;
        for (var i = 0 ; i < recruitmentNotices.size() ; i+=batch) {
            int end = Math.min(i + batch, recruitmentNotices.size());
            List<RecruitmentNotice> chunk = recruitmentNotices.subList(i, end);
            upsert(config, chunk);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }
    }

    @Transactional
    public void upsert(GenerateContentConfig config, List<RecruitmentNotice> recruitmentNotices) {
        // req text 구성
        List<CategoryClassifyRequestDto> requestDtoList = recruitmentNotices.stream()
                .map(CategoryClassifyRequestDto::from)
                .toList();
        String requestText;
        try {
            requestText = objectMapper.writeValueAsString(requestDtoList);
            log.info("request text = " + requestText);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("internal server error", exception.getCause());
        }
        ///

        // update 수행
        try {
            GenerateContentResponse response = geminiClient.models.generateContent(GEMINI_MODEL, requestText, config);
            log.info("gemini response = " + response.text());

            List<CategoryClassifyResponseDto> responseDto = objectMapper.readValue(response.text(),
                    new TypeReference<List<CategoryClassifyResponseDto>>() {
                    });

            for (var dto : responseDto) {
                RecruitmentNotice recruitmentNotice = recruitmentNotices.stream()
                        .filter(a -> a.getRecruitmentNoticeId().equals(dto.recruitmentNoticeId()))
                        .findFirst()
                        .orElseGet(() -> null);

                if (Objects.nonNull(recruitmentNotice)) {
                    recruitmentNotice.updateStandardCategory(dto.standardCategory());
                } else {
                    log.error("invalid gemini response");
                }
            }
        } catch (ClientException clientException) {
            // Too many request 뜨면 무시
          log.error(clientException.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("internal server error", e.getCause());
        }

        recruitmentNoticeRepository.saveAll(recruitmentNotices);
    }
}

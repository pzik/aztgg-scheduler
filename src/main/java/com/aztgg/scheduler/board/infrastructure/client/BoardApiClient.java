package com.aztgg.scheduler.board.infrastructure.client;

import com.aztgg.scheduler.board.infrastructure.config.BoardApiClientConfig;
import com.aztgg.scheduler.subscribeemail.application.dto.MailTemplateBoardDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class BoardApiClient {

    private final RestClient restClient;

    public BoardApiClient(BoardApiClientConfig boardApiClientConfig) {
        this.restClient = boardApiClientConfig.boardRestClient();
    }

    public List<MailTemplateBoardDto> fetchRecentBoards() {
        try {
            Map<String, Object> responseBody = restClient.get()
                .uri("/v1/boards?page=0&pageSize=3")
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (responseBody != null && responseBody.containsKey("list")) {
                List<Map<String, Object>> boardList = (List<Map<String, Object>>) responseBody.get("list");

                return boardList.stream()
                    .map(board -> new MailTemplateBoardDto(
                        ((Number) board.get("boardId")).longValue(),
                        (String) board.get("nickname"),
                        (String) board.get("title"),
                        (String) board.get("content"),
                        java.time.LocalDateTime.parse((String) board.get("createdAt"))
                    ))
                    .toList();
            }
        } catch (Exception e) {
            // API 호출 실패 시 빈 리스트 반환
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }
}


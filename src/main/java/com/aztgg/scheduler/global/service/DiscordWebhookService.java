package com.aztgg.scheduler.global.service;

import com.aztgg.scheduler.global.asset.WebhookType;
import com.aztgg.scheduler.global.logging.AppLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class DiscordWebhookService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${discord.webhook.notice-url}")
    private String noticeWebhookUrl;
    private String hotIssueWebhookUrl;

    public void sendDiscordMessage(WebhookType type, String title, String description, String colorHex) {
        String webhookUrl = switch(type){
            case NOTICE -> noticeWebhookUrl;
            case HOTISSUE -> hotIssueWebhookUrl;
        };

        Map<String, Object> embed = Map.of(
                "title", title,
                "description", description,
                "color", Integer.decode(colorHex)
        );

        Map<String, Object> payload = Map.of("embeds", List.of(embed));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForEntity(webhookUrl, request, String.class);
        } catch(Exception e){
            AppLogger.errorLog("디스코드 Embed 전송 실패 " , e);
        }

    }

}


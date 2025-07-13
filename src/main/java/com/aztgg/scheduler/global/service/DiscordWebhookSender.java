package com.aztgg.scheduler.global.service;

import com.aztgg.scheduler.global.asset.WebhookType;
import com.aztgg.scheduler.global.logging.AppLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class DiscordWebhookSender {
    private final RestClient restClient;

    @Value("${discord.webhook.notice-url}")
    private String noticeWebhookUrl;
    private String hotIssueWebhookUrl;

    public DiscordWebhookSender() {
        this.restClient = RestClient.create();
    }

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

        try {
            restClient.post()
                    .uri(webhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch(Exception e){
            AppLogger.errorLog("디스코드 Embed 전송 실패 ", e);
        }

    }

}


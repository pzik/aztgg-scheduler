package com.aztgg.scheduler.subscribeemail.application.dto;

import java.time.LocalDateTime;

public record MailTemplateBoardDto(Long boardId,
								   String nickname,
								   String title,
								   String content,
								   LocalDateTime createdAt) {
}

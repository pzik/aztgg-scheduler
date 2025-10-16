package com.aztgg.scheduler.subscribeemail.application;

import com.aztgg.scheduler.global.logging.AppLogger;
import com.aztgg.scheduler.subscribeemail.application.dto.MailTemplateNoticeDto;
import com.aztgg.scheduler.subscribeemail.domain.SubscribeEmailCategory;
import com.aztgg.scheduler.subscribeemail.domain.SubscribeEmailRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeEmailService {

    private static final int MINIMUM_NOTIFY_SIZE = 30;

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final SubscribeEmailRepository subscribeEmailRepository;

    public void sendToSubscriber(List<MailTemplateNoticeDto> mailTemplateNotices) {
        for (var subscribeEmail : subscribeEmailRepository.findAll()) {
            Set<String> subscribeCategories = subscribeEmail.getCategories().stream()
                    .map(SubscribeEmailCategory::getCategory)
                    .collect(Collectors.toSet());

            // 24시간 이내 수집된 공고를 우선한다.
            // 만약 공고 개수가 일정개수 미만이라면 기존 공고에서 추가한다.
            LocalDateTime dayAgo = LocalDateTime.now().minusHours(24);
            List<MailTemplateNoticeDto> recentNotices = mailTemplateNotices.stream()
                    .filter(notice -> subscribeCategories.contains(notice.standardCategoryCode()))
                    .filter(notice -> notice.scrapedAt().isAfter(dayAgo))
                    .toList();

            List<MailTemplateNoticeDto> filteredNotices = new ArrayList<>(recentNotices);

            if (filteredNotices.size() < MINIMUM_NOTIFY_SIZE) {
                int remaining = MINIMUM_NOTIFY_SIZE - filteredNotices.size();
                List<MailTemplateNoticeDto> olderNotices = mailTemplateNotices.stream()
                        .filter(notice -> subscribeCategories.contains(notice.standardCategoryCode()))
                        .filter(notice -> notice.scrapedAt().isBefore(dayAgo))
                        .limit(remaining)
                        .toList();

                filteredNotices.addAll(olderNotices);
            }

            // 발송할 공고가 없다면 스킵한다.
            if (!filteredNotices.isEmpty()) {
                send(subscribeEmail.getEmail(), subscribeCategories, filteredNotices);
            }
        }
    }

    private void send(String email, Set<String> tags, List<MailTemplateNoticeDto> mailTemplateNotices) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // getDate
        ZonedDateTime todayDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).atZone(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        String strDate = todayDate.format(formatter);

        // getStrTags
        String strTags = tags.stream()
                .map(s -> "#" + s)
                .collect(Collectors.joining(", "));

        Map<String, Object> contextVars = new HashMap<>();
        contextVars.put("email", email);
        contextVars.put("strDate", strDate);
        contextVars.put("strTags", strTags);
        contextVars.put("mailTemplateNotices", mailTemplateNotices);
        Context context = new Context();
        context.setVariables(contextVars);

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email); // 메일 수신자
            mimeMessageHelper.setSubject("[nklcb]" + strDate + " 맞춤 채용 공고가 도착했어요!"); // 메일 제목
            mimeMessageHelper.setText(templateEngine.process("notice-subscribe-mail-template", context), true); // 메일 본문 내용, HTML 여부
            javaMailSender.send(mimeMessage);

            AppLogger.infoLog("Succeeded to send Email");
        } catch (Exception e) {
            AppLogger.infoLog("Failed to send Email");
            throw new RuntimeException(e);
        }
    }
}

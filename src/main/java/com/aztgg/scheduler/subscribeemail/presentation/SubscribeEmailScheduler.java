package com.aztgg.scheduler.subscribeemail.presentation;

import com.aztgg.scheduler.subscribeemail.application.SubscribeEmailFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscribeEmailScheduler {

    private final SubscribeEmailFacadeService subscribeEmailFacadeService;

    @Scheduled(cron = "0 1 9 * * *", zone = "Asia/Seoul")
    public void notifyToSubscribers() {
        subscribeEmailFacadeService.notifyNewRecruitmentNotices();
    }
}

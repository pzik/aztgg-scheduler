package com.aztgg.scheduler.subscribeemail.application;

import com.aztgg.scheduler.company.domain.Corporate;
import com.aztgg.scheduler.company.domain.PredefinedCompany;
import com.aztgg.scheduler.company.domain.StandardCategory;
import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeService;
import com.aztgg.scheduler.recruitmentnotice.application.dto.RecruitmentNoticeResponseDto;
import com.aztgg.scheduler.subscribeemail.application.dto.MailTemplateNoticeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeEmailFacadeService {

    private final RecruitmentNoticeService recruitmentNoticeService;
    private final SubscribeEmailService subscribeEmailService;

    public void notifyNewRecruitmentNotices() {
        List<RecruitmentNoticeResponseDto> recruitmentNoticeResponses = recruitmentNoticeService.findAllNewestNotices();

        List<MailTemplateNoticeDto> mailTemplateNotices = recruitmentNoticeResponses.stream()
                .map(a -> {
                    String strCorps = a.corporateCodes().stream()
                            .map(Corporate::fromCode)
                            .map(Corporate::getKorean)
                            .collect(Collectors.joining(", "));

                    return new MailTemplateNoticeDto(a.recruitmentNoticeId(), strCorps, a.jobOfferTitle(),
                            a.companyCode(), PredefinedCompany.fromCode(a.companyCode()).getKorean(),
                            a.standardCategory(), StandardCategory.fromCode(a.standardCategory()).getKorean(),
                            a.startAt(), a.endAt(), a.scrapedAt());
                })
                .toList();

        subscribeEmailService.sendToSubscriber(mailTemplateNotices);
    }
}

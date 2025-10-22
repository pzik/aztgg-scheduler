package com.aztgg.scheduler.subscribeemail.application;

import com.aztgg.scheduler.board.infrastructure.client.BoardApiClient;
import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.global.asset.PredefinedCompany;
import com.aztgg.scheduler.global.asset.PredefinedStandardCategory;
import com.aztgg.scheduler.recruitmentnotice.application.RecruitmentNoticeService;
import com.aztgg.scheduler.recruitmentnotice.application.dto.RecruitmentNoticeResponseDto;
import com.aztgg.scheduler.subscribeemail.application.dto.MailTemplateBoardDto;
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
    private final BoardApiClient boardApiClient;

    public void notifyNewRecruitmentNotices() {
        // 채용 공고 데이터 수집
        List<RecruitmentNoticeResponseDto> recruitmentNoticeResponses = recruitmentNoticeService.findAllNewestNotices();

        List<MailTemplateNoticeDto> mailTemplateNotices = recruitmentNoticeResponses.stream()
            .map(a -> {
                String strCorps = a.corporateCodes().stream()
                    .map(PredefinedCorporate::fromCode)
                    .map(PredefinedCorporate::getKorean)
                    .collect(Collectors.joining(", "));

                return new MailTemplateNoticeDto(a.recruitmentNoticeId(), strCorps, a.jobOfferTitle(),
                    a.companyCode(), PredefinedCompany.fromCode(a.companyCode()).getKorean(),
                    a.standardCategory(), PredefinedStandardCategory.fromCode(a.standardCategory()).getKorean(),
                    a.startAt(), a.endAt(), a.scrapedAt());
            })
            .toList();

        // 게시글 데이터 수집
        List<MailTemplateBoardDto> mailTemplateBoards = boardApiClient.fetchRecentBoards();

        // 이메일 발송
        subscribeEmailService.sendToSubscriber(mailTemplateNotices, mailTemplateBoards);
    }
}

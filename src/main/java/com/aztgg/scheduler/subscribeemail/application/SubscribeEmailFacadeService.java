package com.aztgg.scheduler.subscribeemail.application;

import com.aztgg.scheduler.board.infrastructure.client.BoardApiClient;
import com.aztgg.scheduler.global.asset.PredefinedCorporate;
import com.aztgg.scheduler.global.asset.PredefinedCompany;
import com.aztgg.scheduler.global.asset.PredefinedStandardCategory;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeDomainService;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNotice;
import com.aztgg.scheduler.subscribeemail.application.dto.MailTemplateBoardDto;
import com.aztgg.scheduler.subscribeemail.application.dto.MailTemplateNoticeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeEmailFacadeService {

    private final RecruitmentNoticeDomainService recruitmentNoticeDomainService;
    private final SubscribeEmailService subscribeEmailService;
    private final BoardApiClient boardApiClient;

    public void notifyNewRecruitmentNotices() {
        // 채용 공고 데이터 수집
        List<RecruitmentNotice> recruitmentNoticeResponses = recruitmentNoticeDomainService.findAllNewestNotices();

        List<MailTemplateNoticeDto> mailTemplateNotices = recruitmentNoticeResponses.stream()
            .map(a -> {
                String strCorps = StringUtils.commaDelimitedListToSet(a.getCorporateCodes()).stream()
                    .map(PredefinedCorporate::fromCode)
                    .map(PredefinedCorporate::getKorean)
                    .collect(Collectors.joining(", "));

                return new MailTemplateNoticeDto(a.getRecruitmentNoticeId(), strCorps, a.getJobOfferTitle(),
                    a.getCompanyCode(), PredefinedCompany.fromCode(a.getCompanyCode()).getKorean(),
                    a.getStandardCategory(), PredefinedStandardCategory.fromCode(a.getStandardCategory()).getKorean(),
                    a.getStartAt(), a.getEndAt(), a.getScrapedAt());
            })
            .toList();

        // 게시글 데이터 수집
        List<MailTemplateBoardDto> mailTemplateBoards = boardApiClient.fetchRecentBoards();

        // 이메일 발송
        subscribeEmailService.sendToSubscriber(mailTemplateNotices, mailTemplateBoards);
    }
}

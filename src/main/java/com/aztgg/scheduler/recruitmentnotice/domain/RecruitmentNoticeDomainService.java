package com.aztgg.scheduler.recruitmentnotice.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecruitmentNoticeDomainService {

    private final RecruitmentNoticeRepository recruitmentNoticeRepository;

    /**
     * 96시간 이내 수집된 공고 scrapedAt desc 순으로 출력
     */
    public List<RecruitmentNotice> findAllNewestNotices() {
        LocalDateTime target = LocalDateTime.now().minusHours(96);
        return recruitmentNoticeRepository.findAllByScrapedAtAfterOrderByScrapedAtDesc(target);
    }

    public List<RecruitmentNotice> findAllByCompanyCode(String companyCode) {
        return recruitmentNoticeRepository.findAllByCompanyCode(companyCode);
    }

    public Optional<RecruitmentNotice> findById(Long id) {
        return recruitmentNoticeRepository.findById(id);
    }
}

package com.aztgg.scheduler.recruitmentnotice.application;

import com.aztgg.scheduler.company.domain.ScrapGroupCodeType;
import com.aztgg.scheduler.global.util.HashUtils;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNotice;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import com.aztgg.scheduler.recruitmentnotice.domain.scraper.dto.RecruitmentNoticeDto;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Scraper 를 이용해 채용공고를 수집하여 DB에 적재한다.
 * 템플릿 메서드 패턴 도입
 */
@Slf4j
public abstract class RecruitmentNoticeCollectorService {

    private final RecruitmentNoticeRepository recruitmentNoticeRepository;
    private final ScrapGroupCodeType scrapGroupCodeType;

    public RecruitmentNoticeCollectorService(RecruitmentNoticeRepository recruitmentNoticeRepository,
                                             ScrapGroupCodeType scrapGroupCodeType) {
        this.recruitmentNoticeRepository = recruitmentNoticeRepository;
        this.scrapGroupCodeType = scrapGroupCodeType;
    }

    protected abstract List<RecruitmentNoticeDto> result();

    public void collect() {
        log.info("{} scrapGroup collect start", scrapGroupCodeType.name());
        long startTime = System.currentTimeMillis();

        List<RecruitmentNoticeDto> scrapResult = this.result();
        List<RecruitmentNotice> beforeRecruitmentNotices = recruitmentNoticeRepository.findByScrapGroupCode(scrapGroupCodeType.name());
        List<RecruitmentNotice> afterRecruitmentNotices = scrapResult.stream()
                .map(item -> RecruitmentNotice.builder()
                        .jobOfferTitle(item.getJobOfferTitle())
                        .companyCode(scrapGroupCodeType.getCompany().name())
                        .scrapGroupCode(scrapGroupCodeType.name())
                        .categories(item.getCategories())
                        .corporateCodes(item.getCorporateCodes())
                        .startAt(item.getStartAt())
                        .endAt(item.getEndAt())
                        .hash(HashUtils.encrypt(String.valueOf(item.hashCode())))
                        .url(item.getUrl())
                        .build())
                .toList();

        // delete (detail url이 before엔 존재했으나 after에 존재하지 않으면 삭제)
        Set<String> afterUrls = afterRecruitmentNotices.stream()
                .map(RecruitmentNotice::getUrl)
                .collect(Collectors.toSet());

        Set<Long> deleteTargetIds = beforeRecruitmentNotices.stream()
                .filter(item -> !afterUrls.contains(item.getUrl()))
                .map(RecruitmentNotice::getRecruitmentNoticeId)
                .collect(Collectors.toSet());

        recruitmentNoticeRepository.deleteAllById(deleteTargetIds);
        beforeRecruitmentNotices.removeIf(recruitmentNotice -> deleteTargetIds.contains(recruitmentNotice.getRecruitmentNoticeId()));

        // upsert (detail url은 일치하나 hash값이 틀어졌다면 upsert)
        for (var recruitmentNotice : afterRecruitmentNotices) {
            RecruitmentNotice before = beforeRecruitmentNotices.stream()
                    .filter(item -> item.getUrl().equals(recruitmentNotice.getUrl()))
                    .findFirst()
                    .orElse(null);

            // before url이 존재
            if (Objects.nonNull(before)) {
                recruitmentNotice.updateRecruitmentNoticeIdAndCountAndScrapedAt(before.getRecruitmentNoticeId(),
                        before.getClickCount(), before.getScrapedAt());
            }
        }

        recruitmentNoticeRepository.saveAll(afterRecruitmentNotices);

        long endTime = System.currentTimeMillis(); // 코드 끝난 시간
        long durationTimeSec = endTime - startTime;
        log.info("{} collect end, duration = {} sec", scrapGroupCodeType.name(), (durationTimeSec / 1000));
    }
}

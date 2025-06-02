package com.aztgg.scheduler.recruitmentnotice.application;

import com.aztgg.scheduler.recruitmentnotice.application.dto.RecruitmentNoticeResponseDto;
import com.aztgg.scheduler.recruitmentnotice.domain.RecruitmentNoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentNoticeService {

    private final RecruitmentNoticeRepository recruitmentNoticeRepository;

    /**
     * 96시간 이내 수집된 공고 scrapedAt desc 순으로 출력
     */
    public List<RecruitmentNoticeResponseDto> findAllNewestNotices() {
        LocalDateTime target = LocalDateTime.now().minusHours(96);

        return recruitmentNoticeRepository.findAllByScrapedAtAfterOrderByScrapedAtDesc(target).stream()
                .map(RecruitmentNoticeResponseDto::from)
                .collect(Collectors.toList());
    }
}

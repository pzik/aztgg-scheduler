package com.aztgg.scheduler.subscribeemail.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record MailTemplateNoticeDto(Long recruitmentNoticeId,
                                    String strCorps,
                                    String jobOfferTitle,
                                    String companyCode,
                                    String strCompany,
                                    String standardCategoryCode,
                                    String strStandardCategory,
                                    LocalDateTime startAt,
                                    LocalDateTime endAt,
                                    LocalDateTime scrapedAt) {
}

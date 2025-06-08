package com.aztgg.scheduler.recruitmentnotice.domain.categoryclassifier;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StandardCategoryClassifyFormat(String standardCategory, String keyword) {
}

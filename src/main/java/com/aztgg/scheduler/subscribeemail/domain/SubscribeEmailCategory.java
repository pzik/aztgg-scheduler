package com.aztgg.scheduler.subscribeemail.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Table("subscribe_email_category")
public class SubscribeEmailCategory {

    @Id
    @Column("subscribeEmailCategoryId")
    private Long subscribeEmailCategoryId;

    @Column("subscribeEmailId")
    private Long subscribeEmailId;

    @Column("category")
    private String category;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("modifiedAt")
    private LocalDateTime modifiedAt;
}


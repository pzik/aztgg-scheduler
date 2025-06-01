package com.aztgg.scheduler.subscribeemail.domain;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Table("subscribe_email")
public class SubscribeEmail {

    @Id
    @Column("subscribeEmailId")
    private Long subscribeEmailId;

    @Column("email")
    private String email;

    @MappedCollection(idColumn = "subscribeEmailId")
    private Set<SubscribeEmailCategory> categories;

    @Column("createdAt")
    private LocalDateTime createdAt;

    @Column("modifiedAt")
    private LocalDateTime modifiedAt;
}


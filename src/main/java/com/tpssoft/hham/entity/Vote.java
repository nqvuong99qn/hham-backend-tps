package com.tpssoft.hham.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.time.ZonedDateTime;

@Entity(name = "votes")
@IdClass(VoteId.class)
@Data
@NoArgsConstructor
public class Vote {
    @Id
    private Integer userId;

    @Id
    private Integer optionId;

    @Column(columnDefinition = "text")
    private String note;

    @Column(nullable = false, columnDefinition = "timestamp with time zone DEFAULT NOW()")
    private ZonedDateTime createdOn = ZonedDateTime.now();

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(nullable = false, name = "userId")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @MapsId("optionId")
    @ManyToOne
    @JoinColumn(nullable = false, name = "optionId")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Option option;


    public Vote(Integer userId, Integer optionId) {
        this.userId = userId;
        this.optionId = optionId;
    }

    public Vote(VoteId voteId) {
        this(voteId.getUserId(), voteId.getOptionId());
    }
}

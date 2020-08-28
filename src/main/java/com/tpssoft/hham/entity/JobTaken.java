package com.tpssoft.hham.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.time.ZonedDateTime;

@Entity(name = "jobs_taken")
@IdClass(JobTakenId.class)
@Data
public class JobTaken {
    @Id
    private Integer userId;

    @Id
    private Integer jobTitleId;

    @Column(nullable = false, columnDefinition = "timestamp with time zone DEFAULT NOW()")
    private ZonedDateTime startOn = ZonedDateTime.now();

    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime endOn;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(nullable = false, name = "userId")
    private User user;

    @MapsId("jobTitleId")
    @ManyToOne
    @JoinColumn(nullable = false, name = "jobTitleId")
    private JobTitle jobTitle;

    public JobTaken() {
    }

    public JobTaken(int userId, int jobTitleId) {
        this.userId = userId;
        this.jobTitleId = jobTitleId;
    }
}

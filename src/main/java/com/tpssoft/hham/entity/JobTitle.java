package com.tpssoft.hham.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity(name = "job_titles")
@Data
public class JobTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(columnDefinition = "decimal(12, 2) NOT NULL CHECK(monthly_amount >= 0)")
    private BigDecimal monthlyAmount = BigDecimal.ZERO;

    @Column(nullable = false, columnDefinition = "timestamp with time zone DEFAULT NOW()")
    private ZonedDateTime createdOn = ZonedDateTime.now();

    private ZonedDateTime archivedOn;

    @OneToMany(mappedBy = "jobTitle")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<JobTaken> jobsTaken;
}

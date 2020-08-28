package com.tpssoft.hham.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity(name = "funds")
@Data
public class Fund {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "decimal(12, 2) NOT NULL CHECK(amount >= 0)")
    private BigDecimal amount;

    @Column(nullable = false, columnDefinition = "timestamp with time zone DEFAULT NOW()")
    private ZonedDateTime createdOn = ZonedDateTime.now();

    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime archivedOn;

    @ManyToOne
    @JoinColumn(nullable = false, name = "projectId")
    private Project project;

    public Fund() {
    }

    public Fund(String name, Project project) {
        this.name = name;
        this.amount = BigDecimal.ZERO;
        this.project = project;
    }
}

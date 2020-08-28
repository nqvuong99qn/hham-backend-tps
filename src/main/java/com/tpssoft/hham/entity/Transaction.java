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

@Entity(name = "transactions")
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private TransactionType type;

    @ManyToOne
    @JoinColumn(nullable = false, name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(nullable = false, name = "fundId")
    private Fund fund;

    @ManyToOne
    @JoinColumn(name = "activityId")
    private Activity activity;

    @Column(columnDefinition = "decimal(12, 2) NOT NULL CHECK(amount >= 0)")
    private BigDecimal amount;

    @Column(nullable = false, columnDefinition = "text DEFAULT ''")
    private String memo = "";

    @Column(nullable = false, columnDefinition = "timestamp with time zone DEFAULT NOW()")
    private ZonedDateTime createdOn = ZonedDateTime.now();
}

package com.tpssoft.hham.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity(name = "options")
@Data
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "activityId")
    private Activity activity;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Image image;

    @Column(columnDefinition = "decimal(12, 2) NOT NULL CHECK(price >= 0)")
    private BigDecimal price;

    @Column(nullable = false, columnDefinition = "timestamp with time zone DEFAULT NOW()")
    private ZonedDateTime createdOn = ZonedDateTime.now();

    @OneToMany(mappedBy = "option")
    @ToString.Exclude
    private Set<Vote> votes;
}

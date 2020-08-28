package com.tpssoft.hham.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "projects")
@Data
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, columnDefinition = "text default ''")
    private String description = "";

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Image image;

    @Column(nullable = false, columnDefinition = "timestamp with time zone DEFAULT NOW()")
    private ZonedDateTime createdOn = ZonedDateTime.now();

    @Column(columnDefinition = "timestamp with time zone")
    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime archivedOn;

    @OneToMany(mappedBy = "project")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<Fund> funds = new ArrayList<>();

    @ManyToMany(mappedBy = "projects")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Activity> activities = new HashSet<>();

    @OneToMany(mappedBy = "project")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Membership> memberships = new HashSet<>();
}

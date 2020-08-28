package com.tpssoft.hham.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "activities")
@Data
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "typeId")
    private ActivityType type;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, columnDefinition = "text DEFAULT ''")
    private String description;

    @Enumerated(EnumType.STRING)
    private EndStatus endStatus;

    @Column(nullable = false, columnDefinition = "text DEFAULT ''")
    private String endNote = "";

    @Column(nullable = false, columnDefinition = "timestamp with time zone DEFAULT NOW()")
    private ZonedDateTime createdOn = ZonedDateTime.now();

    @Column(columnDefinition = "timestamp with time zone")
    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime lockedOn;

    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime archivedOn;

    @ManyToMany
    @JoinTable(
            name = "projects_activities",
            joinColumns = @JoinColumn(nullable = false, name = "activityId"),
            inverseJoinColumns = @JoinColumn(nullable = false, name = "projectId")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Project> projects = new HashSet<>();

    @OneToMany(mappedBy = "activity")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Option> options;

}

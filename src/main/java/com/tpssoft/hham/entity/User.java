package com.tpssoft.hham.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 128, unique = true)
    private String username;

    // Including the salt and encoder information
    @Column(nullable = false, columnDefinition = "text")
    private String password;

    @Column(nullable = false, unique = true)
    private String email = "";

    @Column(nullable = false, columnDefinition = "boolean DEFAULT false")
    private boolean admin = false;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(nullable = false, columnDefinition = "timestamp with time zone DEFAULT NOW()")
    private ZonedDateTime createdOn = ZonedDateTime.now();

    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime deactivatedOn;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Image image;

    @OneToMany(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<JobTaken> jobsTaken;

    @OneToMany(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Membership> memberships;

    @OneToMany(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Vote> votes;

    @OneToMany(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Transaction> transactions;

    @OneToMany(mappedBy = "user")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Session> sessions;
}

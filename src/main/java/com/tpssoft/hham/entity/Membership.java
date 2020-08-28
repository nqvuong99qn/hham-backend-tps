package com.tpssoft.hham.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import java.time.ZonedDateTime;

@Entity(name = "memberships")
@IdClass(MembershipId.class)
@Data
@NoArgsConstructor
public class Membership {
    @Id
    private Integer userId;

    @Id
    private Integer projectId;

    @Column(nullable = false, columnDefinition = "boolean DEFAULT false")
    private boolean admin = false;

    @Column(nullable = false, columnDefinition = "timestamp with time zone DEFAULT NOW()")
    private ZonedDateTime joinedOn = ZonedDateTime.now();

    private ZonedDateTime leftOn;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(nullable = false, name = "userId")
    private User user;

    @MapsId("projectId")
    @ManyToOne
    @JoinColumn(nullable = false, name = "projectId")
    private Project project;

    public Membership(int userId, int projectId) {
        this.userId = userId;
        this.projectId = projectId;
    }

    public Membership(int userId, int projectId, boolean admin) {
        this(userId, projectId);
        this.admin = admin;
    }

    public Membership(MembershipId membershipId) {
        this(membershipId.getUserId(), membershipId.getProjectId());
    }
}

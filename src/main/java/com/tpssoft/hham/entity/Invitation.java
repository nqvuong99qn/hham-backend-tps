package com.tpssoft.hham.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity(name = "invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
    @Id
    @Column(nullable = false, length = 128)
    private String token;

    @Column(nullable = false)
    private String email;

    private Integer projectId;

    @Column(columnDefinition = "timestamp with time zone")
    private ZonedDateTime expireOn;
}

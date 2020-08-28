package com.tpssoft.hham.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity(name = "images")
@Data
public class Image {
    @Id
    @Column(nullable = false, length = 128)
    private String digest;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, columnDefinition = "text")
    private String content; // Base64 encoded

    // Only one of them has value
    @OneToMany(mappedBy = "image")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<User> users;

    @OneToMany(mappedBy = "image")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Project> projects;

    @OneToMany(mappedBy = "image")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Option> options;
}

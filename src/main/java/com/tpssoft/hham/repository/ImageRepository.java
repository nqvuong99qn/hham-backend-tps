package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {
    @Query("select i from images i join i.users u where u.id = :id")
    Optional<Image> findByUserId(int id);

    @Query("select i from images i join i.options o where o.id = :id")
    Optional<Image> findByOptionId(int id);

    @Query("select i from images i join i.projects p where p.id = :id")
    Optional<Image> findByProjectId(int id);
}

package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobTitleRepository extends JpaRepository<JobTitle, Integer> {
    @Query("select jt from job_titles jt " +
            "join fetch jt.jobsTaken jk " +
            "join fetch jk.user u " +
            "where u.id=:id and jk.endOn is null")
    Optional<JobTitle> findByUserId(int id);

    boolean existsByName(String name);
}

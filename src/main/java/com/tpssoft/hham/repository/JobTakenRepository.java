package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.JobTaken;
import com.tpssoft.hham.entity.JobTakenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobTakenRepository extends JpaRepository<JobTaken, JobTakenId> {
    @Query("select jk from jobs_taken jk join fetch jk.user u where u.id=:id and jk.endOn is null")
    Optional<JobTaken> findByUserId(int id);
}

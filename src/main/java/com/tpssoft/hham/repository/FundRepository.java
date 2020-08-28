package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.Fund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FundRepository extends JpaRepository<Fund, Integer> {
    @Query("select f from funds f join f.project p where p.id = :projectId order by f.id")
    List<Fund> findByProjectId(int projectId);
}

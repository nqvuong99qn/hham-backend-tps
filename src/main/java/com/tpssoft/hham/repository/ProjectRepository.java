package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.Fund;
import com.tpssoft.hham.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query("select f from projects p join p.funds f where p.id = :id order by f.id")
    List<Fund> getFunds(int id);

    @Query("select p from projects p " +
            "join p.memberships m " +
            "join m.user u " +
            "where u.id = :id " +
            "order by p.id")
    List<Project> findByUserId(int id);

    @Query("select p from projects p " +
            "join p.activities a " +
            "where a.id = :id " +
            "order by p.id")
    List<Project> findByActivityId(int id);
    boolean existsByName(String name);
}

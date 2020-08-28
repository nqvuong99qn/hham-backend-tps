package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    Optional<Activity> findById(Integer id);

    @Query("select a from activities a order by a.id")
    List<Activity> findAll();

    @Query("select a from activities a join a.options o where o.id = :optionId")
    Optional<Activity> findByOptionId(Integer optionId);
}

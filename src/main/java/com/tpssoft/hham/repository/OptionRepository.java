package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {
    @Query("select o from options o order by o.id")
    List<Option> findAll();
}

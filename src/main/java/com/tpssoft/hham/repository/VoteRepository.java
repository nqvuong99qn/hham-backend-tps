package com.tpssoft.hham.repository;


import com.tpssoft.hham.entity.Vote;
import com.tpssoft.hham.entity.VoteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, VoteId> {
    List<Vote> findAllByUserId(Integer userId);
}

package com.tpssoft.hham.repository;

import com.tpssoft.hham.dto.TransactionDto;
import com.tpssoft.hham.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("select t from transactions t " +
            "join t.user u join t.fund f where u.id = :id and f.project.id = :projectId" +
            " order by  t.createdOn desc ")
    List<Transaction> findByUserId(int id, int projectId);
}

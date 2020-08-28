package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("select u from users u " +
            "join u.memberships m " +
            "join m.project p " +
            "where p.id = :projectId and m.admin = true " +
            "order by u.id")
    List<User> getAdminsOfProject(int projectId);

    @Query("select u from users u " +
            "join u.memberships m " +
            "join m.project p " +
            "where p.id = :projectId " +
            "order by u.id")
    List<User> getMembersOfProject(int projectId);

    boolean existsByEmail(String email);
}

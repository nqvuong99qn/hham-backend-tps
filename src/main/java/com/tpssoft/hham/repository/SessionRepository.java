package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Integer> {
    Optional<Session> findByToken(String token);

    /**
     * Get all login sessions of a specific user, including expired ones.
     *
     * @param id ID of the user
     *
     * @return List of sessions belonging to the specified user
     */
    @Query("select s from sessions s join fetch users u where u.id = :id")
    List<Session> getSessionsByUserId(int id);

    /**
     * Get all non-expired login sessions of a specific user
     *
     * @param id ID of the user
     *
     * @return List of non-expired sessions belonging to the specified user
     */
    @Query("select s from sessions s join fetch users u where u.id = :id and s.expiredOn > current_timestamp")
    List<Session> getNonexpiredSessionsByUserId(int id);
}

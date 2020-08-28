package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, String> {
    void deleteAllByEmail(String email);
}

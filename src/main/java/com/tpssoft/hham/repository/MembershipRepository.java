package com.tpssoft.hham.repository;

import com.tpssoft.hham.entity.Membership;
import com.tpssoft.hham.entity.MembershipId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, MembershipId> {
}

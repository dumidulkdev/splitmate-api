package com.splitmate.settlement;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SettlementRepository extends JpaRepository<Settlement, UUID> {

    @EntityGraph(attributePaths = {"fromMember", "toMember"})
    List<Settlement> findByGroupIdOrderByCreatedAtAsc(UUID groupId);

    boolean existsByFromMemberIdOrToMemberId(UUID fromMemberId, UUID toMemberId);
}

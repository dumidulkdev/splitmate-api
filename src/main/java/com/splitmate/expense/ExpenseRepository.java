package com.splitmate.expense;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @EntityGraph(attributePaths = {"paidBy", "shares", "shares.member"})
    List<Expense> findByGroupIdOrderByCreatedAtAsc(UUID groupId);

    @EntityGraph(attributePaths = {"paidBy", "shares", "shares.member"})
    Optional<Expense> findByIdAndGroupId(UUID id, UUID groupId);

    boolean existsByPaidById(UUID memberId);

    boolean existsByShares_Member_Id(UUID memberId);
}

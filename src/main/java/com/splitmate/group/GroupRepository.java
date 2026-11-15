package com.splitmate.group;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {

    @EntityGraph(attributePaths = "members")
    @Query("select g from Group g order by g.createdAt desc")
    List<Group> findAllWithMembers();

    @EntityGraph(attributePaths = "members")
    @Query("select g from Group g where g.id = :id")
    Optional<Group> findWithMembersById(UUID id);
}

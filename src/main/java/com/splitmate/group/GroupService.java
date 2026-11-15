package com.splitmate.group;

import com.splitmate.common.BadRequestException;
import com.splitmate.common.NotFoundException;
import com.splitmate.group.dto.AddMemberRequest;
import com.splitmate.group.dto.CreateGroupRequest;
import com.splitmate.group.dto.GroupResponse;
import com.splitmate.group.dto.MemberResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groups;
    private final MemberRepository members;

    public GroupService(GroupRepository groups, MemberRepository members) {
        this.groups = groups;
        this.members = members;
    }

    @Transactional
    public GroupResponse create(CreateGroupRequest request) {
        Group group = groups.save(new Group(request.name().trim()));
        return GroupMapper.toResponse(group);
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> list() {
        return groups.findAllWithMembers().stream()
                .map(GroupMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public GroupResponse get(UUID groupId) {
        return GroupMapper.toResponse(requireGroup(groupId));
    }

    @Transactional
    public MemberResponse addMember(UUID groupId, AddMemberRequest request) {
        Group group = requireGroup(groupId);
        String name = request.name().trim();
        if (members.existsByGroupIdAndNameIgnoreCase(groupId, name)) {
            throw duplicateMember(name);
        }
        Member member = group.addMember(name);
        try {
            members.saveAndFlush(member);
        } catch (DataIntegrityViolationException ex) {
            throw duplicateMember(name);
        }
        return GroupMapper.toMember(member);
    }

    @Transactional
    public void removeMember(UUID groupId, UUID memberId) {
        Member member = requireMember(groupId, memberId);
        member.getGroup().removeMember(member);
    }

    @Transactional(readOnly = true)
    public Group requireGroup(UUID groupId) {
        return groups.findWithMembersById(groupId)
                .orElseThrow(() -> new NotFoundException("Group %s was not found".formatted(groupId)));
    }

    @Transactional(readOnly = true)
    public Member requireMember(UUID groupId, UUID memberId) {
        return members.findByIdAndGroupId(memberId, groupId)
                .orElseThrow(() -> new NotFoundException("Member %s was not found in this group".formatted(memberId)));
    }

    private BadRequestException duplicateMember(String name) {
        return new BadRequestException("A member named '%s' is already in this group".formatted(name));
    }
}

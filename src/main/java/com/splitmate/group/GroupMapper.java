package com.splitmate.group;

import com.splitmate.group.dto.GroupResponse;
import com.splitmate.group.dto.MemberResponse;

import java.util.Comparator;
import java.util.List;

final class GroupMapper {

    private GroupMapper() {
    }

    static GroupResponse toResponse(Group group) {
        List<MemberResponse> members = group.getMembers().stream()
                .sorted(Comparator.comparing(Member::getName, String.CASE_INSENSITIVE_ORDER))
                .map(GroupMapper::toMember)
                .toList();
        return new GroupResponse(group.getId(), group.getName(), group.getCreatedAt(), members);
    }

    static MemberResponse toMember(Member member) {
        return new MemberResponse(member.getId(), member.getName());
    }
}

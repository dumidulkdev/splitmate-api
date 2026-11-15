package com.splitmate.settlement;

import com.splitmate.common.BadRequestException;
import com.splitmate.common.Money;
import com.splitmate.group.GroupService;
import com.splitmate.group.Member;
import com.splitmate.settlement.dto.RecordSettlementRequest;
import com.splitmate.settlement.dto.SettlementResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SettlementService {

    private final SettlementRepository settlements;
    private final GroupService groups;

    public SettlementService(SettlementRepository settlements, GroupService groups) {
        this.settlements = settlements;
        this.groups = groups;
    }

    @Transactional
    public SettlementResponse record(UUID groupId, RecordSettlementRequest request) {
        if (request.fromMemberId().equals(request.toMemberId())) {
            throw new BadRequestException("A member cannot settle with themselves");
        }
        Member from = groups.requireMember(groupId, request.fromMemberId());
        Member to = groups.requireMember(groupId, request.toMemberId());
        Settlement settlement = new Settlement(
                from.getGroup(),
                from,
                to,
                Money.normalize(request.amount()),
                blankToNull(request.note()));
        return SettlementMapper.toResponse(settlements.saveAndFlush(settlement));
    }

    @Transactional(readOnly = true)
    public List<SettlementResponse> list(UUID groupId) {
        groups.requireGroup(groupId);
        return settlements.findByGroupIdOrderByCreatedAtAsc(groupId).stream()
                .map(SettlementMapper::toResponse)
                .toList();
    }

    private String blankToNull(String note) {
        if (note == null) {
            return null;
        }
        String trimmed = note.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

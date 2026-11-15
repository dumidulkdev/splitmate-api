package com.splitmate.settlement;

import com.splitmate.group.MemberActivity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class SettlementMemberActivity implements MemberActivity {

    private final SettlementRepository settlements;

    SettlementMemberActivity(SettlementRepository settlements) {
        this.settlements = settlements;
    }

    @Override
    public boolean hasActivity(UUID memberId) {
        return settlements.existsByFromMemberIdOrToMemberId(memberId, memberId);
    }
}

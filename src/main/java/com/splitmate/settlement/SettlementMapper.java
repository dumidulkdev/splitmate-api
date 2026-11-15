package com.splitmate.settlement;

import com.splitmate.settlement.dto.SettlementResponse;

final class SettlementMapper {

    private SettlementMapper() {
    }

    static SettlementResponse toResponse(Settlement settlement) {
        return new SettlementResponse(
                settlement.getId(),
                settlement.getFromMember().getId(),
                settlement.getFromMember().getName(),
                settlement.getToMember().getId(),
                settlement.getToMember().getName(),
                settlement.getAmount(),
                settlement.getNote(),
                settlement.getCreatedAt());
    }
}

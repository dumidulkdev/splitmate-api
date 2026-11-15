package com.splitmate.settlement;

import com.fasterxml.jackson.databind.JsonNode;
import com.splitmate.common.Money;
import com.splitmate.support.ApiTestBase;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SettleUpApiTest extends ApiTestBase {

    @Test
    void suggestsBothDebtorsPayThePersonWhoCoveredDinner() throws Exception {
        UUID groupId = createGroup("Weekend in Porto");
        UUID ana = addMember(groupId, "Ana");
        UUID bruno = addMember(groupId, "Bruno");
        UUID carla = addMember(groupId, "Carla");
        postJson("/api/groups/" + groupId + "/expenses", Map.of(
                "description", "Dinner",
                "amount", new BigDecimal("90.00"),
                "paidBy", ana,
                "splitType", "EQUAL",
                "shares", List.of(
                        Map.of("memberId", ana),
                        Map.of("memberId", bruno),
                        Map.of("memberId", carla))));

        JsonNode suggestions = getJson("/api/groups/" + groupId + "/settle-up");

        assertThat(suggestions).hasSize(2);
        List<String> payers = new ArrayList<>();
        for (JsonNode suggestion : suggestions) {
            assertThat(suggestion.get("toMemberName").asText()).isEqualTo("Ana");
            assertThat(Money.normalize(suggestion.get("amount").decimalValue())).isEqualByComparingTo("30.00");
            payers.add(suggestion.get("fromMemberName").asText());
        }
        assertThat(payers).containsExactlyInAnyOrder("Bruno", "Carla");
    }
}

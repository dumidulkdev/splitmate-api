package com.splitmate.balance;

import com.fasterxml.jackson.databind.JsonNode;
import com.splitmate.common.Money;
import com.splitmate.support.ApiTestBase;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceApiTest extends ApiTestBase {

    @Test
    void payerIsCreditedForEveryoneElsesShare() throws Exception {
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

        JsonNode balances = getJson("/api/groups/" + groupId + "/balances");

        assertThat(balanceOf(balances, "Ana")).isEqualByComparingTo("60.00");
        assertThat(balanceOf(balances, "Bruno")).isEqualByComparingTo("-30.00");
        assertThat(balanceOf(balances, "Carla")).isEqualByComparingTo("-30.00");
        assertThat(sum(balances)).isEqualByComparingTo("0.00");
    }

    private BigDecimal balanceOf(JsonNode balances, String name) {
        for (JsonNode row : balances) {
            if (name.equals(row.get("memberName").asText())) {
                return Money.normalize(row.get("balance").decimalValue());
            }
        }
        throw new AssertionError("no balance for " + name);
    }

    private BigDecimal sum(JsonNode balances) {
        BigDecimal total = Money.zero();
        for (JsonNode row : balances) {
            total = total.add(Money.normalize(row.get("balance").decimalValue()));
        }
        return total;
    }
}

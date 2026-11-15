package com.splitmate.settlement;

import com.fasterxml.jackson.databind.JsonNode;
import com.splitmate.common.Money;
import com.splitmate.support.ApiTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SettlementApiTest extends ApiTestBase {

    @Test
    void recordingTheSuggestedPaymentsSettlesTheGroup() throws Exception {
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
        postJson("/api/groups/" + groupId + "/expenses", Map.of(
                "description", "Tickets",
                "amount", new BigDecimal("40.00"),
                "paidBy", bruno,
                "splitType", "PERCENTAGE",
                "shares", List.of(
                        Map.of("memberId", ana, "value", new BigDecimal("50.00")),
                        Map.of("memberId", carla, "value", new BigDecimal("50.00")))));

        JsonNode balances = getJson("/api/groups/" + groupId + "/balances");
        assertThat(amount(balances, "Ana")).isEqualByComparingTo("40.00");
        assertThat(amount(balances, "Bruno")).isEqualByComparingTo("10.00");
        assertThat(amount(balances, "Carla")).isEqualByComparingTo("-50.00");

        JsonNode suggestions = getJson("/api/groups/" + groupId + "/settle-up");
        assertThat(suggestions).hasSize(2);
        assertThat(suggestions.get(0).get("fromMemberName").asText()).isEqualTo("Carla");
        assertThat(suggestions.get(0).get("toMemberName").asText()).isEqualTo("Ana");
        assertThat(Money.normalize(suggestions.get(0).get("amount").decimalValue())).isEqualByComparingTo("40.00");
        assertThat(suggestions.get(1).get("fromMemberName").asText()).isEqualTo("Carla");
        assertThat(suggestions.get(1).get("toMemberName").asText()).isEqualTo("Bruno");
        assertThat(Money.normalize(suggestions.get(1).get("amount").decimalValue())).isEqualByComparingTo("10.00");

        for (JsonNode suggestion : suggestions) {
            Map<String, Object> payment = new HashMap<>();
            payment.put("fromMemberId", suggestion.get("fromMemberId").asText());
            payment.put("toMemberId", suggestion.get("toMemberId").asText());
            payment.put("amount", suggestion.get("amount").decimalValue());
            payment.put("note", "Settle up");
            postJson("/api/groups/" + groupId + "/settlements", payment);
        }

        JsonNode settled = getJson("/api/groups/" + groupId + "/balances");
        assertThat(amount(settled, "Ana")).isEqualByComparingTo("0.00");
        assertThat(amount(settled, "Bruno")).isEqualByComparingTo("0.00");
        assertThat(amount(settled, "Carla")).isEqualByComparingTo("0.00");
        assertThat(getJson("/api/groups/" + groupId + "/settle-up")).isEmpty();
        assertThat(getJson("/api/groups/" + groupId + "/settlements")).hasSize(2);

        mvc.perform(delete("/api/groups/" + groupId + "/members/" + carla))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This member still has expenses or settlements in the group"));
    }

    @Test
    void rejectsASettlementWithYourself() throws Exception {
        UUID groupId = createGroup("House");
        UUID ana = addMember(groupId, "Ana");

        mvc.perform(post("/api/groups/" + groupId + "/settlements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fromMemberId":"%s","toMemberId":"%s","amount":10.00}
                                """.formatted(ana, ana)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("A member cannot settle with themselves"))
                .andExpect(jsonPath("$.path").value("/api/groups/" + groupId + "/settlements"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(0)));
    }

    private BigDecimal amount(JsonNode balances, String name) {
        for (JsonNode row : balances) {
            if (name.equals(row.get("memberName").asText())) {
                return Money.normalize(row.get("balance").decimalValue());
            }
        }
        throw new AssertionError("no balance for " + name);
    }
}

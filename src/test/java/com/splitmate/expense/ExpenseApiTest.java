package com.splitmate.expense;

import com.fasterxml.jackson.databind.JsonNode;
import com.splitmate.common.Money;
import com.splitmate.support.ApiTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ExpenseApiTest extends ApiTestBase {

    @Test
    void equalSplitStoresSharesThatSumToTheTotal() throws Exception {
        UUID groupId = createGroup("Weekend in Porto");
        UUID ana = addMember(groupId, "Ana");
        UUID bruno = addMember(groupId, "Bruno");
        UUID carla = addMember(groupId, "Carla");

        JsonNode created = postJson("/api/groups/" + groupId + "/expenses", equal(ana, bruno, carla));

        assertThat(created.get("splitType").asText()).isEqualTo("EQUAL");
        assertThat(created.get("paidByName").asText()).isEqualTo("Ana");
        assertThat(sumShares(created)).isEqualByComparingTo("100.00");
        assertThat(shareAmounts(created)).containsExactlyInAnyOrder(
                new BigDecimal("33.34"), new BigDecimal("33.33"), new BigDecimal("33.33"));

        mvc.perform(get("/api/groups/" + groupId + "/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Dinner"));
    }

    @Test
    void exactSplitMustSumToTheTotal() throws Exception {
        UUID groupId = createGroup("House");
        UUID ana = addMember(groupId, "Ana");
        UUID bruno = addMember(groupId, "Bruno");

        mvc.perform(post("/api/groups/" + groupId + "/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Groceries",
                                  "amount": 90.00,
                                  "paidBy": "%s",
                                  "splitType": "EXACT",
                                  "shares": [
                                    {"memberId": "%s", "value": 30.00},
                                    {"memberId": "%s", "value": 50.00}
                                  ]
                                }
                                """.formatted(ana, ana, bruno)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Exact amounts sum to 80.00 but the expense is 90.00"))
                .andExpect(jsonPath("$.path").value("/api/groups/" + groupId + "/expenses"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(0)));
    }

    @Test
    void percentageSplitRoundsAndKeepsTheTotal() throws Exception {
        UUID groupId = createGroup("Tickets");
        UUID ana = addMember(groupId, "Ana");
        UUID bruno = addMember(groupId, "Bruno");

        JsonNode created = postJson("/api/groups/" + groupId + "/expenses", Map.of(
                "description", "Train",
                "amount", new BigDecimal("40.00"),
                "paidBy", bruno,
                "splitType", "PERCENTAGE",
                "shares", List.of(
                        Map.of("memberId", ana, "value", new BigDecimal("50.00")),
                        Map.of("memberId", bruno, "value", new BigDecimal("50.00")))));

        assertThat(created.get("paidBy").asText()).isEqualTo(bruno.toString());
        assertThat(sumShares(created)).isEqualByComparingTo("40.00");
        assertThat(shareAmounts(created)).containsExactly(new BigDecimal("20.00"), new BigDecimal("20.00"));
    }

    @Test
    void deletesAnExpenseAndThenBlocksNothingForThatMember() throws Exception {
        UUID groupId = createGroup("House");
        UUID ana = addMember(groupId, "Ana");
        UUID bruno = addMember(groupId, "Bruno");
        JsonNode created = postJson("/api/groups/" + groupId + "/expenses", Map.of(
                "description", "Coffee",
                "amount", new BigDecimal("6.00"),
                "paidBy", ana,
                "splitType", "EQUAL",
                "shares", List.of(Map.of("memberId", ana), Map.of("memberId", bruno))));

        mvc.perform(delete("/api/groups/" + groupId + "/members/" + bruno))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("This member still has expenses or settlements in the group"));

        mvc.perform(delete("/api/groups/" + groupId + "/expenses/" + created.get("id").asText()))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/groups/" + groupId + "/expenses/" + created.get("id").asText()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Expense " + created.get("id").asText() + " was not found in this group"));

        mvc.perform(delete("/api/groups/" + groupId + "/members/" + bruno))
                .andExpect(status().isNoContent());
    }

    @Test
    void unknownSplitTypeIsABadRequest() throws Exception {
        UUID groupId = createGroup("House");
        UUID ana = addMember(groupId, "Ana");

        mvc.perform(post("/api/groups/" + groupId + "/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Coffee",
                                  "amount": 3.00,
                                  "paidBy": "%s",
                                  "splitType": "BANANA",
                                  "shares": [{"memberId": "%s"}]
                                }
                                """.formatted(ana, ana)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unknown value 'BANANA'"))
                .andExpect(jsonPath("$.fieldErrors", hasSize(0)));
    }

    private Map<String, Object> equal(UUID payer, UUID... others) {
        List<Map<String, Object>> shares = new java.util.ArrayList<>();
        shares.add(Map.of("memberId", payer));
        for (UUID other : others) {
            shares.add(Map.of("memberId", other));
        }
        return Map.of(
                "description", "Dinner",
                "amount", new BigDecimal("100.00"),
                "paidBy", payer,
                "splitType", "EQUAL",
                "shares", shares);
    }

    private BigDecimal sumShares(JsonNode expense) {
        BigDecimal sum = BigDecimal.ZERO;
        for (JsonNode share : expense.get("shares")) {
            sum = sum.add(Money.normalize(share.get("amount").decimalValue()));
        }
        return sum;
    }

    private List<BigDecimal> shareAmounts(JsonNode expense) {
        List<BigDecimal> amounts = new java.util.ArrayList<>();
        for (JsonNode share : expense.get("shares")) {
            amounts.add(Money.normalize(share.get("amount").decimalValue()));
        }
        return amounts;
    }
}

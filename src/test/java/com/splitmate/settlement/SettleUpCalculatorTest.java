package com.splitmate.settlement;

import com.splitmate.balance.dto.BalanceResponse;
import com.splitmate.settlement.dto.SettlementSuggestion;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SettleUpCalculatorTest {

    private final SettleUpCalculator calculator = new SettleUpCalculator();

    @Test
    void matchesTheLargestCreditorWithTheLargestDebtor() {
        List<SettlementSuggestion> transfers = calculator.suggest(List.of(
                balance(1, "Ana", "60.00"),
                balance(2, "Bruno", "10.00"),
                balance(3, "Carla", "-40.00"),
                balance(4, "Diego", "-30.00")));

        assertThat(transfers).containsExactly(
                transfer(3, "Carla", 1, "Ana", "40.00"),
                transfer(4, "Diego", 1, "Ana", "20.00"),
                transfer(4, "Diego", 2, "Bruno", "10.00"));
    }

    @Test
    void breaksTiesByMemberId() {
        List<SettlementSuggestion> transfers = calculator.suggest(List.of(
                balance(1, "Ana", "30.00"),
                balance(2, "Bruno", "20.00"),
                balance(4, "Diego", "-25.00"),
                balance(3, "Carla", "-25.00")));

        assertThat(transfers).containsExactly(
                transfer(3, "Carla", 1, "Ana", "25.00"),
                transfer(4, "Diego", 2, "Bruno", "20.00"),
                transfer(4, "Diego", 1, "Ana", "5.00"));
    }

    @Test
    void returnsNothingWhenEveryoneIsSettled() {
        assertThat(calculator.suggest(List.of(
                balance(1, "Ana", "0.00"),
                balance(2, "Bruno", "0.00")))).isEmpty();
    }

    private static BalanceResponse balance(int member, String name, String amount) {
        return new BalanceResponse(id(member), name, new BigDecimal(amount));
    }

    private static SettlementSuggestion transfer(int from, String fromName, int to, String toName, String amount) {
        return new SettlementSuggestion(id(from), fromName, id(to), toName, new BigDecimal(amount));
    }

    private static UUID id(int n) {
        return UUID.fromString("00000000-0000-0000-0000-%012d".formatted(n));
    }
}

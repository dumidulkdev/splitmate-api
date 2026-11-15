package com.splitmate.expense.strategy;

import com.splitmate.common.BadRequestException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExactSplitStrategyTest {

    private final ExactSplitStrategy strategy = new ExactSplitStrategy();

    @Test
    void acceptsAmountsThatSumToTheTotal() {
        UUID ana = id(1);
        UUID bruno = id(2);

        List<ComputedShare> shares = strategy.split(new BigDecimal("90.00"), List.of(
                new ShareSpec(bruno, new BigDecimal("60.00")),
                new ShareSpec(ana, new BigDecimal("30.00"))));

        assertThat(shares).containsExactly(
                new ComputedShare(ana, new BigDecimal("30.00")),
                new ComputedShare(bruno, new BigDecimal("60.00")));
    }

    @Test
    void rejectsAmountsThatDoNotSumToTheTotal() {
        assertThatThrownBy(() -> strategy.split(new BigDecimal("90.00"), List.of(
                new ShareSpec(id(1), new BigDecimal("30.00")),
                new ShareSpec(id(2), new BigDecimal("50.00")))))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Exact amounts sum to 80.00 but the expense is 90.00");
    }

    @Test
    void rejectsAMissingAmount() {
        assertThatThrownBy(() -> strategy.split(new BigDecimal("10.00"), List.of(new ShareSpec(id(1), null))))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Each participant needs a share amount");
    }

    private static UUID id(int n) {
        return UUID.fromString("00000000-0000-0000-0000-%012d".formatted(n));
    }
}

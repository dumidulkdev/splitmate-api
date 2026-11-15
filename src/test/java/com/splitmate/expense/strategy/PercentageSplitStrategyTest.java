package com.splitmate.expense.strategy;

import com.splitmate.common.BadRequestException;
import com.splitmate.common.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PercentageSplitStrategyTest {

    private final PercentageSplitStrategy strategy = new PercentageSplitStrategy();

    @Test
    void splitsAnEvenPercentageWithoutLeftoverCents() {
        List<ComputedShare> shares = strategy.split(new BigDecimal("40.00"), List.of(
                new ShareSpec(id(2), new BigDecimal("50")),
                new ShareSpec(id(1), new BigDecimal("50"))));

        assertThat(shares).containsExactly(
                new ComputedShare(id(1), new BigDecimal("20.00")),
                new ComputedShare(id(2), new BigDecimal("20.00")));
    }

    @Test
    void handsTheRoundingCentToTheLowestMemberId() {
        UUID low = id(1);
        UUID mid = id(2);
        UUID high = id(3);

        List<ComputedShare> shares = strategy.split(new BigDecimal("10.00"), List.of(
                new ShareSpec(mid, new BigDecimal("33.33")),
                new ShareSpec(high, new BigDecimal("33.33")),
                new ShareSpec(low, new BigDecimal("33.34"))));

        assertThat(shares).containsExactly(
                new ComputedShare(low, new BigDecimal("3.34")),
                new ComputedShare(mid, new BigDecimal("3.33")),
                new ComputedShare(high, new BigDecimal("3.33")));
        assertThat(Money.sum(shares.stream().map(ComputedShare::amount).toList()))
                .isEqualByComparingTo("10.00");
    }

    @Test
    void rejectsPercentagesThatDoNotSumToOneHundred() {
        assertThatThrownBy(() -> strategy.split(new BigDecimal("10.00"), List.of(
                new ShareSpec(id(1), new BigDecimal("33.33")),
                new ShareSpec(id(2), new BigDecimal("33.33")),
                new ShareSpec(id(3), new BigDecimal("33.33")))))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Percentages sum to 99.99 but must sum to 100");
    }

    private static UUID id(int n) {
        return UUID.fromString("00000000-0000-0000-0000-%012d".formatted(n));
    }
}

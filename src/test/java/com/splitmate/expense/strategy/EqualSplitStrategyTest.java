package com.splitmate.expense.strategy;

import com.splitmate.common.BadRequestException;
import com.splitmate.common.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EqualSplitStrategyTest {

    private final EqualSplitStrategy strategy = new EqualSplitStrategy();

    @Test
    void givesTheExtraCentToTheLowestMemberId() {
        UUID low = id(1);
        UUID mid = id(2);
        UUID high = id(3);

        List<ComputedShare> shares = strategy.split(new BigDecimal("100.00"), List.of(
                new ShareSpec(high, null),
                new ShareSpec(low, null),
                new ShareSpec(mid, null)));

        assertThat(shares).containsExactly(
                new ComputedShare(low, new BigDecimal("33.34")),
                new ComputedShare(mid, new BigDecimal("33.33")),
                new ComputedShare(high, new BigDecimal("33.33")));
        assertThat(sum(shares)).isEqualByComparingTo("100.00");
    }

    @Test
    void samePeopleInAnotherOrderProduceTheSameShares() {
        UUID low = id(1);
        UUID high = id(2);
        List<ShareSpec> forward = List.of(new ShareSpec(low, null), new ShareSpec(high, null));
        List<ShareSpec> backward = List.of(new ShareSpec(high, null), new ShareSpec(low, null));

        assertThat(strategy.split(new BigDecimal("10.00"), forward))
                .isEqualTo(strategy.split(new BigDecimal("10.00"), backward));
    }

    @Test
    void rejectsAnEmptySplit() {
        assertThatThrownBy(() -> strategy.split(new BigDecimal("10.00"), List.of()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Add at least one participant");
    }

    private static BigDecimal sum(List<ComputedShare> shares) {
        return Money.sum(shares.stream().map(ComputedShare::amount).toList());
    }

    private static UUID id(int n) {
        return UUID.fromString("00000000-0000-0000-0000-%012d".formatted(n));
    }
}

package com.splitmate.common;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void halfEvenRoundsToTheEvenCent() {
        assertThat(Money.normalize(new BigDecimal("1.225"))).isEqualByComparingTo("1.22");
        assertThat(Money.normalize(new BigDecimal("1.235"))).isEqualByComparingTo("1.24");
    }

    @Test
    void equalSplitGivesLeftoverCentsToTheFrontOfTheList() {
        List<BigDecimal> shares = Money.splitEqually(new BigDecimal("100.00"), 3);

        assertThat(shares).containsExactly(
                new BigDecimal("33.34"),
                new BigDecimal("33.33"),
                new BigDecimal("33.33"));
        assertThat(Money.sum(shares)).isEqualByComparingTo("100.00");
    }

    @Test
    void aSingleCentGoesToTheFirstShare() {
        List<BigDecimal> shares = Money.splitEqually(new BigDecimal("0.01"), 3);

        assertThat(shares).containsExactly(
                new BigDecimal("0.01"),
                new BigDecimal("0.00"),
                new BigDecimal("0.00"));
    }

    @Test
    void allocatePullsCentsBackWhenRoundingOvershoots() {
        List<BigDecimal> shares = Money.allocate(
                new BigDecimal("0.02"),
                List.of(new BigDecimal("0.01"), new BigDecimal("0.01"), new BigDecimal("0.01")));

        assertThat(shares).containsExactly(
                new BigDecimal("0.00"),
                new BigDecimal("0.01"),
                new BigDecimal("0.01"));
        assertThat(Money.sum(shares)).isEqualByComparingTo("0.02");
    }

    @Test
    void rejectsAnEmptyAllocation() {
        assertThatThrownBy(() -> Money.allocate(new BigDecimal("10.00"), List.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

package com.splitmate.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Money is always an exact number of cents. Rounding uses half-even, then any
 * leftover cent is handed out in list order so a split still adds up to the total.
 */
public final class Money {

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;

    private Money() {
    }

    public static BigDecimal zero() {
        return BigDecimal.ZERO.setScale(SCALE, ROUNDING);
    }

    public static BigDecimal normalize(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("amount is required");
        }
        return amount.setScale(SCALE, ROUNDING);
    }

    public static boolean same(BigDecimal left, BigDecimal right) {
        return normalize(left).compareTo(normalize(right)) == 0;
    }

    public static BigDecimal sum(Collection<BigDecimal> amounts) {
        BigDecimal total = zero();
        for (BigDecimal amount : amounts) {
            total = total.add(normalize(amount));
        }
        return total;
    }

    public static List<BigDecimal> splitEqually(BigDecimal total, int parts) {
        if (parts < 1) {
            throw new IllegalArgumentException("parts must be at least 1");
        }
        BigDecimal each = normalize(total).divide(BigDecimal.valueOf(parts), SCALE, ROUNDING);
        return allocate(total, Collections.nCopies(parts, each));
    }

    /**
     * Round each raw share to cents, then move leftover cents through the list
     * from the front until the shares sum to {@code total}.
     */
    public static List<BigDecimal> allocate(BigDecimal total, List<BigDecimal> rawShares) {
        if (rawShares == null || rawShares.isEmpty()) {
            throw new IllegalArgumentException("at least one share is required");
        }
        long target = toCents(total);
        long[] cents = new long[rawShares.size()];
        long rounded = 0;
        for (int i = 0; i < rawShares.size(); i++) {
            cents[i] = toCents(rawShares.get(i));
            rounded += cents[i];
        }
        long drift = target - rounded;
        int index = 0;
        int steps = 0;
        int limit = cents.length * 100_000;
        while (drift != 0) {
            if (steps++ > limit) {
                throw new IllegalStateException("could not allocate " + normalize(total).toPlainString());
            }
            if (drift > 0) {
                cents[index] += 1;
                drift -= 1;
            } else if (cents[index] > 0) {
                cents[index] -= 1;
                drift += 1;
            }
            index = (index + 1) % cents.length;
        }
        List<BigDecimal> shares = new ArrayList<>(cents.length);
        for (long cent : cents) {
            shares.add(fromCents(cent));
        }
        return List.copyOf(shares);
    }

    private static long toCents(BigDecimal amount) {
        return normalize(amount).movePointRight(SCALE).longValueExact();
    }

    private static BigDecimal fromCents(long cents) {
        return BigDecimal.valueOf(cents, SCALE);
    }
}

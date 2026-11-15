package com.splitmate.expense.strategy;

import com.splitmate.common.BadRequestException;
import com.splitmate.common.Money;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

final class SplitChecks {

    private SplitChecks() {
    }

    static List<ShareSpec> ordered(BigDecimal total, List<ShareSpec> specs) {
        if (total == null || total.signum() <= 0) {
            throw new BadRequestException("Expense total must be positive");
        }
        if (specs == null || specs.isEmpty()) {
            throw new BadRequestException("Add at least one participant");
        }
        return specs.stream()
                .sorted(Comparator.comparing(ShareSpec::memberId))
                .toList();
    }

    static BigDecimal requiredValue(ShareSpec spec, String label) {
        if (spec.value() == null) {
            throw new BadRequestException("Each participant needs a " + label);
        }
        BigDecimal value = Money.normalize(spec.value());
        if (value.signum() < 0) {
            throw new BadRequestException("A " + label + " cannot be negative");
        }
        return value;
    }
}

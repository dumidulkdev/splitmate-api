package com.splitmate.expense.strategy;

import java.math.BigDecimal;
import java.util.UUID;

public record ShareSpec(UUID memberId, BigDecimal value) {
}

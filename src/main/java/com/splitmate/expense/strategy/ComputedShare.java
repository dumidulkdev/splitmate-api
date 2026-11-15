package com.splitmate.expense.strategy;

import java.math.BigDecimal;
import java.util.UUID;

public record ComputedShare(UUID memberId, BigDecimal amount) {
}

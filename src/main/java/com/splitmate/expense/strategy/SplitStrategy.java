package com.splitmate.expense.strategy;

import com.splitmate.expense.SplitType;

import java.math.BigDecimal;
import java.util.List;

public interface SplitStrategy {

    SplitType type();

    List<ComputedShare> split(BigDecimal total, List<ShareSpec> specs);
}

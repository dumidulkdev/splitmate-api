package com.splitmate.expense.strategy;

import com.splitmate.common.Money;
import com.splitmate.expense.SplitType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class EqualSplitStrategy implements SplitStrategy {

    @Override
    public SplitType type() {
        return SplitType.EQUAL;
    }

    @Override
    public List<ComputedShare> split(BigDecimal total, List<ShareSpec> specs) {
        List<ShareSpec> ordered = SplitChecks.ordered(total, specs);
        List<BigDecimal> amounts = Money.splitEqually(total, ordered.size());
        List<ComputedShare> shares = new ArrayList<>(ordered.size());
        for (int i = 0; i < ordered.size(); i++) {
            shares.add(new ComputedShare(ordered.get(i).memberId(), amounts.get(i)));
        }
        return shares;
    }
}

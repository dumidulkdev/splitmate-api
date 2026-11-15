package com.splitmate.expense.strategy;

import com.splitmate.common.BadRequestException;
import com.splitmate.common.Money;
import com.splitmate.expense.SplitType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class PercentageSplitStrategy implements SplitStrategy {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

    @Override
    public SplitType type() {
        return SplitType.PERCENTAGE;
    }

    @Override
    public List<ComputedShare> split(BigDecimal total, List<ShareSpec> specs) {
        List<ShareSpec> ordered = SplitChecks.ordered(total, specs);
        BigDecimal normalizedTotal = Money.normalize(total);
        List<BigDecimal> raw = new ArrayList<>(ordered.size());
        List<BigDecimal> percents = new ArrayList<>(ordered.size());
        for (ShareSpec spec : ordered) {
            BigDecimal percent = SplitChecks.requiredValue(spec, "percentage");
            percents.add(percent);
            raw.add(normalizedTotal.multiply(percent)
                    .divide(BigDecimal.valueOf(100), Money.SCALE, Money.ROUNDING));
        }
        BigDecimal percentSum = Money.sum(percents);
        if (percentSum.compareTo(ONE_HUNDRED) != 0) {
            throw new BadRequestException("Percentages sum to %s but must sum to 100"
                    .formatted(percentSum.toPlainString()));
        }
        List<BigDecimal> amounts = Money.allocate(normalizedTotal, raw);
        List<ComputedShare> shares = new ArrayList<>(ordered.size());
        for (int i = 0; i < ordered.size(); i++) {
            shares.add(new ComputedShare(ordered.get(i).memberId(), amounts.get(i)));
        }
        return shares;
    }
}

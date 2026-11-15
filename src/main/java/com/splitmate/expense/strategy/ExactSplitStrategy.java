package com.splitmate.expense.strategy;

import com.splitmate.common.BadRequestException;
import com.splitmate.common.Money;
import com.splitmate.expense.SplitType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExactSplitStrategy implements SplitStrategy {

    @Override
    public SplitType type() {
        return SplitType.EXACT;
    }

    @Override
    public List<ComputedShare> split(BigDecimal total, List<ShareSpec> specs) {
        List<ShareSpec> ordered = SplitChecks.ordered(total, specs);
        List<ComputedShare> shares = new ArrayList<>(ordered.size());
        for (ShareSpec spec : ordered) {
            shares.add(new ComputedShare(spec.memberId(), SplitChecks.requiredValue(spec, "share amount")));
        }
        BigDecimal sum = Money.sum(shares.stream().map(ComputedShare::amount).toList());
        if (!Money.same(sum, total)) {
            throw new BadRequestException("Exact amounts sum to %s but the expense is %s"
                    .formatted(sum.toPlainString(), Money.normalize(total).toPlainString()));
        }
        return shares;
    }
}

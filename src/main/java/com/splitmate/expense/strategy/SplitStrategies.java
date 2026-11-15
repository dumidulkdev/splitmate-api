package com.splitmate.expense.strategy;

import com.splitmate.common.BadRequestException;
import com.splitmate.expense.SplitType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class SplitStrategies {

    private final Map<SplitType, SplitStrategy> byType;

    public SplitStrategies(List<SplitStrategy> strategies) {
        Map<SplitType, SplitStrategy> map = new EnumMap<>(SplitType.class);
        for (SplitStrategy strategy : strategies) {
            map.put(strategy.type(), strategy);
        }
        this.byType = Map.copyOf(map);
    }

    public SplitStrategy forType(SplitType type) {
        SplitStrategy strategy = byType.get(type);
        if (strategy == null) {
            throw new BadRequestException("Unsupported split type " + type);
        }
        return strategy;
    }
}

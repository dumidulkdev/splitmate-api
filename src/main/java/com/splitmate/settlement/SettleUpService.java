package com.splitmate.settlement;

import com.splitmate.balance.BalanceService;
import com.splitmate.settlement.dto.SettlementSuggestion;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SettleUpService {

    private final BalanceService balances;
    private final SettleUpCalculator calculator;

    public SettleUpService(BalanceService balances, SettleUpCalculator calculator) {
        this.balances = balances;
        this.calculator = calculator;
    }

    @Transactional(readOnly = true)
    public List<SettlementSuggestion> suggest(UUID groupId) {
        return calculator.suggest(balances.forGroup(groupId));
    }
}

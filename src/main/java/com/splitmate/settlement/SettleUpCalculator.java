package com.splitmate.settlement;

import com.splitmate.balance.dto.BalanceResponse;
import com.splitmate.common.Money;
import com.splitmate.settlement.dto.SettlementSuggestion;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

/**
 * Greedy settle-up. Repeatedly match the largest creditor with the largest
 * debtor and transfer the smaller of the two amounts. Ties break by member id.
 */
@Component
public class SettleUpCalculator {

    public List<SettlementSuggestion> suggest(List<BalanceResponse> balances) {
        PriorityQueue<Party> creditors = new PriorityQueue<>(Party.largestFirst());
        PriorityQueue<Party> debtors = new PriorityQueue<>(Party.largestFirst());
        for (BalanceResponse balance : balances) {
            BigDecimal net = Money.normalize(balance.balance());
            if (net.signum() > 0) {
                creditors.add(new Party(balance.memberId(), balance.memberName(), net));
            } else if (net.signum() < 0) {
                debtors.add(new Party(balance.memberId(), balance.memberName(), net.abs()));
            }
        }

        List<SettlementSuggestion> transfers = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Party creditor = creditors.poll();
            Party debtor = debtors.poll();
            BigDecimal amount = Money.normalize(creditor.amount.min(debtor.amount));
            if (amount.signum() == 0) {
                continue;
            }
            transfers.add(new SettlementSuggestion(
                    debtor.id, debtor.name, creditor.id, creditor.name, amount));
            requeue(creditors, creditor, creditor.amount.subtract(amount));
            requeue(debtors, debtor, debtor.amount.subtract(amount));
        }
        return transfers;
    }

    private void requeue(PriorityQueue<Party> queue, Party party, BigDecimal remaining) {
        BigDecimal left = Money.normalize(remaining);
        if (left.signum() > 0) {
            queue.add(party.withAmount(left));
        }
    }

    private record Party(UUID id, String name, BigDecimal amount) {

        Party withAmount(BigDecimal next) {
            return new Party(id, name, next);
        }

        static Comparator<Party> largestFirst() {
            return Comparator.comparing(Party::amount).reversed().thenComparing(Party::id);
        }
    }
}

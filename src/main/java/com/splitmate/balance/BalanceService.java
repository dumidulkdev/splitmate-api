package com.splitmate.balance;

import com.splitmate.balance.dto.BalanceResponse;
import com.splitmate.common.Money;
import com.splitmate.expense.Expense;
import com.splitmate.expense.ExpenseRepository;
import com.splitmate.expense.ExpenseShare;
import com.splitmate.group.Group;
import com.splitmate.group.GroupService;
import com.splitmate.group.Member;
import com.splitmate.settlement.Settlement;
import com.splitmate.settlement.SettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BalanceService {

    private final GroupService groups;
    private final ExpenseRepository expenses;
    private final SettlementRepository settlements;

    public BalanceService(GroupService groups, ExpenseRepository expenses, SettlementRepository settlements) {
        this.groups = groups;
        this.expenses = expenses;
        this.settlements = settlements;
    }

    @Transactional(readOnly = true)
    public List<BalanceResponse> forGroup(UUID groupId) {
        Group group = groups.requireGroup(groupId);
        Map<UUID, BigDecimal> nets = new LinkedHashMap<>();
        for (Member member : group.getMembers()) {
            nets.put(member.getId(), Money.zero());
        }
        for (Expense expense : expenses.findByGroupIdOrderByCreatedAtAsc(groupId)) {
            credit(nets, expense.getPaidBy().getId(), expense.getAmount());
            for (ExpenseShare share : expense.getShares()) {
                credit(nets, share.getMember().getId(), share.getAmount().negate());
            }
        }
        for (Settlement settlement : settlements.findByGroupIdOrderByCreatedAtAsc(groupId)) {
            credit(nets, settlement.getFromMember().getId(), settlement.getAmount());
            credit(nets, settlement.getToMember().getId(), settlement.getAmount().negate());
        }
        return group.getMembers().stream()
                .sorted(Comparator.comparing(Member::getName, String.CASE_INSENSITIVE_ORDER))
                .map(member -> new BalanceResponse(
                        member.getId(),
                        member.getName(),
                        nets.getOrDefault(member.getId(), Money.zero())))
                .toList();
    }

    private void credit(Map<UUID, BigDecimal> nets, UUID memberId, BigDecimal amount) {
        nets.merge(memberId, Money.normalize(amount), (current, delta) -> Money.normalize(current.add(delta)));
    }
}

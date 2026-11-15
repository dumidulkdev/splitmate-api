package com.splitmate.expense;

import com.splitmate.common.BadRequestException;
import com.splitmate.common.Money;
import com.splitmate.common.NotFoundException;
import com.splitmate.expense.dto.CreateExpenseRequest;
import com.splitmate.expense.dto.ExpenseResponse;
import com.splitmate.expense.dto.ShareRequest;
import com.splitmate.expense.strategy.ComputedShare;
import com.splitmate.expense.strategy.ShareSpec;
import com.splitmate.expense.strategy.SplitStrategies;
import com.splitmate.group.Group;
import com.splitmate.group.GroupService;
import com.splitmate.group.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository expenses;
    private final GroupService groups;
    private final SplitStrategies strategies;

    public ExpenseService(ExpenseRepository expenses, GroupService groups, SplitStrategies strategies) {
        this.expenses = expenses;
        this.groups = groups;
        this.strategies = strategies;
    }

    @Transactional
    public ExpenseResponse add(UUID groupId, CreateExpenseRequest request) {
        Group group = groups.requireGroup(groupId);
        Member payer = groups.requireMember(groupId, request.paidBy());
        Map<UUID, Member> participants = loadParticipants(groupId, request.shares());
        List<ShareSpec> specs = request.shares().stream()
                .map(share -> new ShareSpec(share.memberId(), share.value()))
                .toList();
        List<ComputedShare> computed = strategies.forType(request.splitType()).split(request.amount(), specs);

        Expense expense = new Expense(
                group,
                request.description().trim(),
                Money.normalize(request.amount()),
                payer,
                request.splitType());
        for (ComputedShare share : computed) {
            Member member = participants.get(share.memberId());
            if (member == null) {
                throw new BadRequestException("A member can only appear once in a split");
            }
            expense.addShare(member, share.amount());
        }
        return ExpenseMapper.toResponse(expenses.saveAndFlush(expense));
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> list(UUID groupId) {
        groups.requireGroup(groupId);
        return expenses.findByGroupIdOrderByCreatedAtAsc(groupId).stream()
                .map(ExpenseMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse get(UUID groupId, UUID expenseId) {
        return ExpenseMapper.toResponse(requireExpense(groupId, expenseId));
    }

    @Transactional
    public void delete(UUID groupId, UUID expenseId) {
        expenses.delete(requireExpense(groupId, expenseId));
    }

    private Expense requireExpense(UUID groupId, UUID expenseId) {
        return expenses.findByIdAndGroupId(expenseId, groupId)
                .orElseThrow(() -> new NotFoundException("Expense %s was not found in this group".formatted(expenseId)));
    }

    private Map<UUID, Member> loadParticipants(UUID groupId, List<ShareRequest> shares) {
        Map<UUID, Member> participants = new LinkedHashMap<>();
        for (ShareRequest share : shares) {
            if (participants.containsKey(share.memberId())) {
                throw new BadRequestException("A member can only appear once in a split");
            }
            participants.put(share.memberId(), groups.requireMember(groupId, share.memberId()));
        }
        return participants;
    }
}

package com.splitmate.expense;

import com.splitmate.group.MemberActivity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class ExpenseMemberActivity implements MemberActivity {

    private final ExpenseRepository expenses;

    ExpenseMemberActivity(ExpenseRepository expenses) {
        this.expenses = expenses;
    }

    @Override
    public boolean hasActivity(UUID memberId) {
        return expenses.existsByPaidById(memberId) || expenses.existsByShares_Member_Id(memberId);
    }
}

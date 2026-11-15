package com.splitmate.expense;

import com.splitmate.expense.dto.ExpenseResponse;
import com.splitmate.expense.dto.ShareResponse;
import com.splitmate.group.Member;

import java.util.Comparator;
import java.util.List;

final class ExpenseMapper {

    private ExpenseMapper() {
    }

    static ExpenseResponse toResponse(Expense expense) {
        List<ShareResponse> shares = expense.getShares().stream()
                .sorted(Comparator.comparing(share -> share.getMember().getName(), String.CASE_INSENSITIVE_ORDER))
                .map(share -> new ShareResponse(share.getMember().getId(), share.getMember().getName(), share.getAmount()))
                .toList();
        Member payer = expense.getPaidBy();
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                payer.getId(),
                payer.getName(),
                expense.getSplitType(),
                shares,
                expense.getCreatedAt());
    }
}

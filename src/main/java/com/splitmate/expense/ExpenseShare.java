package com.splitmate.expense;

import com.splitmate.common.BaseEntity;
import com.splitmate.group.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "expense_shares")
public class ExpenseShare extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    protected ExpenseShare() {
    }

    public ExpenseShare(Expense expense, Member member, BigDecimal amount) {
        this.expense = expense;
        this.member = member;
        this.amount = amount;
    }

    public Expense getExpense() {
        return expense;
    }

    public Member getMember() {
        return member;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}

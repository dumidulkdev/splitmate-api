package com.splitmate.expense;

import com.splitmate.common.BaseEntity;
import com.splitmate.group.Group;
import com.splitmate.group.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "expenses")
public class Expense extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by_id", nullable = false)
    private Member paidBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SplitType splitType;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseShare> shares = new ArrayList<>();

    protected Expense() {
    }

    public Expense(Group group, String description, BigDecimal amount, Member paidBy, SplitType splitType) {
        this.group = group;
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.splitType = splitType;
    }

    public void addShare(Member member, BigDecimal amount) {
        shares.add(new ExpenseShare(this, member, amount));
    }

    public Group getGroup() {
        return group;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Member getPaidBy() {
        return paidBy;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public List<ExpenseShare> getShares() {
        return Collections.unmodifiableList(shares);
    }
}

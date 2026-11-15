package com.splitmate.settlement;

import com.splitmate.common.BaseEntity;
import com.splitmate.group.Group;
import com.splitmate.group.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "settlements")
public class Settlement extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id", nullable = false)
    private Member fromMember;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id", nullable = false)
    private Member toMember;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Column(length = 200)
    private String note;

    protected Settlement() {
    }

    public Settlement(Group group, Member fromMember, Member toMember, BigDecimal amount, String note) {
        this.group = group;
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.amount = amount;
        this.note = note;
    }

    public Group getGroup() {
        return group;
    }

    public Member getFromMember() {
        return fromMember;
    }

    public Member getToMember() {
        return toMember;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getNote() {
        return note;
    }
}

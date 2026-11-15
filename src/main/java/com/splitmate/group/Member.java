package com.splitmate.group;

import com.splitmate.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "members",
        uniqueConstraints = @UniqueConstraint(name = "uk_members_group_name", columnNames = {"group_id", "name"})
)
public class Member extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false, length = 120)
    private String name;

    protected Member() {
    }

    public Member(Group group, String name) {
        this.group = group;
        this.name = name;
    }

    public Group getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }
}

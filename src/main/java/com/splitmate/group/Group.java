package com.splitmate.group;

import com.splitmate.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "expense_groups")
public class Group extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String name;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> members = new ArrayList<>();

    protected Group() {
    }

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public Member addMember(String name) {
        Member member = new Member(this, name);
        members.add(member);
        return member;
    }

    public void removeMember(Member member) {
        members.remove(member);
    }
}

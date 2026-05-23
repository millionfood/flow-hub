package com.ajh.flow.domain;

import com.ajh.flow.common.constant.UserHistoryType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_histories")
@Getter
@NoArgsConstructor
public class UserHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id",nullable = false)
    private User admin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id",nullable = false)
    private User targetUser;

    @Column(nullable = false,length = 20)
    @Enumerated(EnumType.STRING)
    private UserHistoryType type;

    @Column(nullable = false,length = 255)
    private String remark;

    @Builder
    public UserHistory(User admin, User targetUser, UserHistoryType type, String remark) {
        this.admin = admin;
        this.targetUser = targetUser;
        this.type = type;
        this.remark = remark;
    }

}

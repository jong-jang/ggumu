package com.jjh.ggumu.domain.reward.entity;

import com.jjh.ggumu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rewards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reward {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false, length = 100)
    private String reason;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static Reward of(User user, int amount, String reason) {
        Reward reward = new Reward();
        reward.user = user;
        reward.amount = amount;
        reward.reason = reason;
        return reward;
    }

    public void settle() {
        this.status = "SETTLED";
        this.settledAt = LocalDateTime.now();
    }
}

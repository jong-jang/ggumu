package com.jjh.ggumu.domain.like.entity;

import com.jjh.ggumu.domain.routine.entity.Routine;
import com.jjh.ggumu.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "routine_likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(RoutineLikeId.class)
public class RoutineLike {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static RoutineLike of(User user, Routine routine) {
        RoutineLike like = new RoutineLike();
        like.user = user;
        like.routine = routine;
        return like;
    }
}

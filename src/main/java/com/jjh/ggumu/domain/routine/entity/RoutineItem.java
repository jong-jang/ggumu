package com.jjh.ggumu.domain.routine.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "routine_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoutineItem {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "order_num", nullable = false)
    private int orderNum;

    public static RoutineItem create(Routine routine, String title, int orderNum) {
        RoutineItem item = new RoutineItem();
        item.routine = routine;
        item.title = title;
        item.orderNum = orderNum;
        return item;
    }
}

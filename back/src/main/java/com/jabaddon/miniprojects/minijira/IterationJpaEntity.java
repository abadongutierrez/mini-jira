package com.jabaddon.miniprojects.minijira;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "iterations")
class IterationJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, name = "start_at")
    LocalDateTime startAt;

    @Column(nullable = false, name = "end_at")
    LocalDateTime endAt;

    @Column(nullable = false, name = "task_group_id")
    Long taskGroupId;
}

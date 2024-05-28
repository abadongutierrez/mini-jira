package com.jabaddon.miniprojects.minijira;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "tasks")
class TaskJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "id")
    Long id;

    @Column(nullable = false, name = "name")
    String name;

    @Column(nullable = true, name = "description")
    String description;

    @Column(nullable = true, name = "estimation")
    Double estimation;

    @Column(nullable = true, name = "task_group_id")
    Long taskGroupId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskJpaEntity that = (TaskJpaEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
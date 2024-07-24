package com.jabaddon.miniprojects.minijira.tasks;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TaskGroupJpaRepository extends JpaRepository<TaskGroupJpaEntity, Long> {
    Optional<TaskGroupJpaEntity> findByName(String name);
}

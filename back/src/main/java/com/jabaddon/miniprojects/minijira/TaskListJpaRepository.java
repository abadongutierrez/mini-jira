package com.jabaddon.miniprojects.minijira;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TaskListJpaRepository extends JpaRepository<TaskListJpaEntity, Long> {
}

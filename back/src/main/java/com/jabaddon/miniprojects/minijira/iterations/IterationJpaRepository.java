package com.jabaddon.miniprojects.minijira.iterations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface IterationJpaRepository extends JpaRepository<IterationJpaEntity, Long>{
    // List<TaskJpaEntity> findByTaskGroupId(Long taskGroupId);
}

package com.jabaddon.miniprojects.minijira;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, Long>{
    List<TaskJpaEntity> findByTaskGroupId(Long taskGroupId);
    Optional<TaskJpaEntity> findByName(String name);
}

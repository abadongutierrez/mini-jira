package com.jabaddon.miniprojects.minijira.tasks;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface TaskJpaRepository extends JpaRepository<TaskJpaEntity, Long>{
    List<TaskJpaEntity> findByTaskGroupId(Long taskGroupId);
    long countByName(String name);
    long countByNameAndIdNot(String name, Long id);
}

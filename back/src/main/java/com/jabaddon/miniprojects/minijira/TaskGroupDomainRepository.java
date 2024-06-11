package com.jabaddon.miniprojects.minijira;

import java.util.List;
import java.util.Optional;

interface TaskGroupDomainRepository {
    Long save(TaskGroup taskList);

    void saveNewTasks(TaskGroup taskList);

    Optional<TaskGroup> findById(Long id);

    List<TaskGroup> findAll();

    Long saveNewIteration(Iteration iteration);

    Optional<TaskGroup> findByName(String name);

    boolean existsTaskByName(String name);
}

package com.jabaddon.miniprojects.minijira.tasks;

import java.util.List;
import java.util.Optional;

interface DomainRepository {
    Long save(TaskGroup taskList);

    void saveNewTasks(TaskGroup taskList);

    Optional<TaskGroup> findById(Long id);

    List<TaskGroup> findAll();

    Optional<TaskGroup> findByName(String name);

    boolean existsTaskByName(String name);

    boolean existsTaskByName(String name, Long taskId);

    void deleteTasks(TaskGroup taskGroup);

    void saveEditedTasks(TaskGroup taskGroup);
}

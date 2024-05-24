package com.jabaddon.miniprojects.minijira;

import java.util.List;
import java.util.Optional;

interface TaskListDomainRepository {
    Long save(TaskList taskList);

    Optional<TaskList> findById(Long id);

    public List<TaskList> findAll();

}

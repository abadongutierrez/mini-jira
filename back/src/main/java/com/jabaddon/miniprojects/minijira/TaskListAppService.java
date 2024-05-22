package com.jabaddon.miniprojects.minijira;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class TaskListAppService {

    private final TaskListDomainRepository taskListDomainRepository;

    public TaskListAppService(TaskListDomainRepository taskListRepository) {
        this.taskListDomainRepository = taskListRepository;
    }

    public Long createTaskList(TaskListRequest request) {
        // TODO create factory
        // only one Backlog type in the database
        TaskList taskList = new TaskList(request.name(), TaskListType.valueOf(request.type()));
        return taskListDomainRepository.save(taskList);
    }

    public Optional<TaskListResponse> findById(Long id) {
        return taskListDomainRepository.findById(id).map(
            t -> new TaskListResponse(t.getId(),
                t.getName(), t.getType().name(), t.getStatus().name()));
    }
}

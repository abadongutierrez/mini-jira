package com.jabaddon.miniprojects.minijira.tasks;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.typemeta.funcj.control.Try;

import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;


@Service
class TaskGroupFactory {
    private final DomainRepository taskGroupRepository;

    public TaskGroupFactory(DomainRepository taskGroupRepository) {
        this.taskGroupRepository = taskGroupRepository;
    }

    public Try<TaskGroup> createTaskGroup(NewTaskGroupRequest request) {
        return validateName(request)
            .flatMap(r -> validateNameDoesNotExist(r))
            .map(r -> new TaskGroup(r.name()));
    }

    private Try<NewTaskGroupRequest> validateNameDoesNotExist(NewTaskGroupRequest request) {
        Optional<TaskGroup> optional = taskGroupRepository.findByName(request.name());
        if (optional.isPresent()) {
            return Try.failure(new IllegalArgumentException("Task Group already exists with that Name"));
        }
        return Try.success(request);
    }

    private Try<NewTaskGroupRequest> validateName(NewTaskGroupRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            return Try.failure(new IllegalArgumentException("Task Group Name cannot be blank"));
        }
        return Try.success(request);
    }

    public Try<String> validateUniqueTaskName(String name, Long taskId) {
        boolean findByName = taskId == null ?
            taskGroupRepository.existsTaskByName(name) : taskGroupRepository.existsTaskByName(name, taskId);
        if (findByName) {
            return Try.failure(new IllegalArgumentException("Task name already exists"));
        }
        return Try.success(name);
    }
}

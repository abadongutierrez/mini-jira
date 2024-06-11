package com.jabaddon.miniprojects.minijira;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.typemeta.funcj.control.Either;
import org.typemeta.funcj.control.Validated;

import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;


@Service
class TaskGroupFactory {
    private final TaskGroupDomainRepository taskGroupRepository;

    public TaskGroupFactory(TaskGroupDomainRepository taskGroupRepository) {
        this.taskGroupRepository = taskGroupRepository;
    }

    public Either<RuntimeException, TaskGroup> createTaskGroup(NewTaskGroupRequest request) {
        return validateName(request)
            .flatMap(r -> validateNameDoesNotExist(r))
            .map(r -> new TaskGroup(r.name()));
    }

    private Either<RuntimeException, NewTaskGroupRequest> validateNameDoesNotExist(NewTaskGroupRequest request) {
        Optional<TaskGroup> optional = taskGroupRepository.findByName(request.name());
        if (optional.isPresent()) {
            return Either.left(new IllegalArgumentException("Task Group already exists"));
        }
        return Either.right(request);
    }

    private Either<RuntimeException, NewTaskGroupRequest> validateName(NewTaskGroupRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            return Either.left(new IllegalArgumentException("Name cannot be blank"));
        }
        return Either.right(request);
    }

    public Either<Exception, String> validateUniqueTaskName(String name) {
        boolean findByName = taskGroupRepository.existsTaskByName(name);
        if (findByName) {
            return Either.left(new IllegalArgumentException("Task name already exists"));
        }
        return Either.right(name);
    }
}

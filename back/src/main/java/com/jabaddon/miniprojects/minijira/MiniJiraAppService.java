package com.jabaddon.miniprojects.minijira;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.typemeta.funcj.control.Either;
import org.typemeta.funcj.control.Try;

import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;
import com.jabaddon.miniprojects.minijira.dto.NewTaskRequest;
import com.jabaddon.miniprojects.minijira.dto.TaskGroupResponse;
import com.jabaddon.miniprojects.minijira.dto.TaskResponse;
import com.jabaddon.miniprojects.minijira.dto.TasksInGroupResponse;
import com.jabaddon.miniprojects.minijira.errors.NotFoundException;

@Service
public class MiniJiraAppService {

    private final TaskGroupDomainRepository taskGroupDomainRepository;
    private final TaskGroupFactory taskGroupFactory;

    public MiniJiraAppService(TaskGroupDomainRepository taskListRepository, TaskGroupFactory taskGroupFactory) {
        this.taskGroupDomainRepository = taskListRepository;
        this.taskGroupFactory = taskGroupFactory;
    }

    public Either<RuntimeException, Long> createTaskGroup(NewTaskGroupRequest request) {
        return taskGroupFactory.createTaskGroup(request)
                .flatMap(newTaskGroup -> Either.of(() -> taskGroupDomainRepository.save(newTaskGroup)));
    }

    /*
    public Either<Exception, Long> createTaskGroup(NewTaskGroupRequest request) {
        TaskGroup newTaskGroup = taskGroupFactory.createTaskGroup(request);
        return taskGroupDomainRepository.save(newTaskGroup);
    }
     */

    public List<TaskGroupResponse> getAllTaskGroups() {
        List<TaskGroup> taskLists = taskGroupDomainRepository.findAll();
        return taskLists.stream()
                .map(this::mapToTaskListResponse)
                .toList();
    }

    public Optional<TaskGroupResponse> findById(Long id) {
        Optional<TaskGroup> taskList = taskGroupDomainRepository.findById(id);
        return taskList.map(this::mapToTaskListResponse);
    }

    private TaskGroupResponse mapToTaskListResponse(TaskGroup taskList) {
        return new TaskGroupResponse(taskList.getId(), taskList.getName(), taskList.getStatus().name());
    }

    public Either<Exception, Long> addTask(NewTaskRequest taskCreationRequest) {
        Optional<TaskGroup> groupById = taskGroupDomainRepository.findById(taskCreationRequest.taskGroupId());
        if (groupById.isPresent()) {
            TaskGroup taskGroup = groupById.get();
            return taskGroupFactory.validateUniqueTaskName(taskCreationRequest.name())
                    .flatMap(_ ->
                        taskGroup.addTask(
                            taskCreationRequest.name(),
                            taskCreationRequest.description(),
                            taskCreationRequest.estimation()))
                    .flatMap(_ -> Either.of(() -> {
                        taskGroupDomainRepository.saveNewTasks(taskGroup);
                        return taskGroup.getId();
                    }));
        } else {
            return Either.left(new NotFoundException("Task Group not found"));
        }
    }

    public Try<TasksInGroupResponse> getTasksInGroup(Long id) {
        Try<Optional<TaskGroup>> of = Try.of(() -> taskGroupDomainRepository.findById(id));
        return of.flatMap(taskGroup -> {
                    if (taskGroup.isEmpty()) {
                        return Try.failure(new NotFoundException("Task Group not found"));
                    }
                    TaskGroup tp = taskGroup.get();
                    TasksInGroupResponse response = new TasksInGroupResponse(
                        tp.getTasks().stream()
                            .map(t -> new TaskResponse(t.getId(), t.getName(), t.getDescription())).toList());
                    return Try.success(response);
                });
    }

    public Long startIteration(Long groupId, LocalDateTime now, LocalDateTime plusDays) {
        Optional<TaskGroup> taskGroup = taskGroupDomainRepository.findById(groupId);
        if (taskGroup.isEmpty()) {
            return null; // TODO return error
        }
        TaskGroup tp = taskGroup.get();
        Iteration iteration = tp.startIteration(now, plusDays);
        return taskGroupDomainRepository.saveNewIteration(iteration);
    }
}

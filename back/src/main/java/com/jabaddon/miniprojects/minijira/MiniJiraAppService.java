package com.jabaddon.miniprojects.minijira;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

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

    public Try<Long> createTaskGroup(NewTaskGroupRequest request) {
        return taskGroupFactory.createTaskGroup(request)
                .flatMap(newTaskGroup -> Try.of(() -> taskGroupDomainRepository.save(newTaskGroup)));
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
        long totalEstimation = taskList.totalEstimation().longValue();
        return new TaskGroupResponse(taskList.getId(), taskList.getName(), taskList.getStatus().name(), totalEstimation);
    }

    public Try<Long> addTask(NewTaskRequest taskCreationRequest) {
        Optional<TaskGroup> groupById = taskGroupDomainRepository.findById(taskCreationRequest.taskGroupId());
        if (groupById.isPresent()) {
            TaskGroup taskGroup = groupById.get();
            return taskGroupFactory.validateUniqueTaskName(taskCreationRequest.name())
                    .flatMap(_ ->
                        taskGroup.addTask(
                            taskCreationRequest.name(),
                            taskCreationRequest.description(),
                            taskCreationRequest.estimation()))
                    .flatMap(_ -> Try.of(() -> {
                        taskGroupDomainRepository.saveNewTasks(taskGroup);
                        return taskGroup.getId();
                    }));
        } else {
            return Try.failure(new NotFoundException("Task Group not found"));
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
                        tp.getTasks().stream().map(this::mapTaskToTaskResponse).toList());
                    return Try.success(response);
                });
    }

    public Try<TaskResponse> getTaskById(Long groupId, Long taskId) {
        return Try.of(() -> {
            TaskGroup taskGroup = taskGroupDomainRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Task Group not found"));
            Task task = taskGroup.getTasks().stream().filter(t -> t.getId().equals(taskId))
                .findFirst().orElseThrow(() -> new NotFoundException("Task not found"));
            return mapTaskToTaskResponse(task);
        });
    }

    public Try<Boolean> deleteTaskInGroup(Long groupId, Long taskId) {
        return Try.of(() -> {
            Optional<TaskGroup> taskGroup = taskGroupDomainRepository.findById(groupId);
            taskGroup.ifPresent(tp -> tp.deleteTask(taskId).orElseThrow());
            taskGroupDomainRepository.deleteTasks(taskGroup.get());
            return true;
        });
    }

    public Try<TaskResponse> editTaskInGroup(Long groupId, Long taskId, NewTaskRequest request) {
        return Try.of(() -> {
            TaskGroup taskGroup = taskGroupDomainRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Task Group not found"));
            taskGroupFactory.validateUniqueTaskName(request.name()).orElseThrow();
            taskGroup.editTask(taskId, request.name(), request.description(), request.estimation()).orElseThrow();
            taskGroupDomainRepository.saveEditedTasks(taskGroup);
            return taskGroupDomainRepository.findById(groupId).get().getTasks()
                .stream().filter(t -> t.getId().equals(taskId)).findFirst().map(this::mapTaskToTaskResponse).get();
        });
    }

    private TaskResponse mapTaskToTaskResponse(Task t) {
        int estimationAsInt = t.getEstimation() != null ? t.getEstimation().intValue() : 0;
        return new TaskResponse(t.getId(), t.getName(), t.getDescription(), estimationAsInt);
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

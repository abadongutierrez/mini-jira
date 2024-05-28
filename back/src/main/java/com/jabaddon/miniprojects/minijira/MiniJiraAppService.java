package com.jabaddon.miniprojects.minijira;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;
import com.jabaddon.miniprojects.minijira.dto.NewTaskRequest;
import com.jabaddon.miniprojects.minijira.dto.TaskGroupResponse;
import com.jabaddon.miniprojects.minijira.dto.TaskResponse;
import com.jabaddon.miniprojects.minijira.dto.TasksInGroupResponse;

@Service
public class MiniJiraAppService {

    private final TaskGroupDomainRepository taskGroupDomainRepository;

    public MiniJiraAppService(TaskGroupDomainRepository taskListRepository) {
        this.taskGroupDomainRepository = taskListRepository;
    }

    public Long createTaskGroup(NewTaskGroupRequest request) {
        // TODO create factory
        // only one Backlog type in the database
        TaskGroup taskList = new TaskGroup(request.name(), TaskGroupType.valueOf(request.type()));
        return taskGroupDomainRepository.save(taskList);
    }

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
        return new TaskGroupResponse(taskList.getId(), taskList.getName(), taskList.getType().name(), taskList.getStatus().name());
    }

    public void addTask(NewTaskRequest taskCreationRequest) {
        taskGroupDomainRepository.findById(taskCreationRequest.taskGroupId())
                .ifPresent(taskGroup -> {
                    taskGroup.addTask(
                        taskCreationRequest.name(),
                        taskCreationRequest.description(),
                        taskCreationRequest.estimation());
                    taskGroupDomainRepository.saveNewTasks(taskGroup);
                });
    }

    public TasksInGroupResponse getTasksInGroup(Long id) {
        Optional<TaskGroup> taskGroup = taskGroupDomainRepository.findById(id);
        if (taskGroup.isEmpty()) {
            return null;
        }
        TaskGroup tp = taskGroup.get();
        TasksInGroupResponse response = new TasksInGroupResponse(
                tp.getTasks().stream().map(t -> new TaskResponse(t.getId(), t.getName(), t.getDescription())).toList());
        return response;
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

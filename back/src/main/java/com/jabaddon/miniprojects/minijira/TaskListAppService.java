package com.jabaddon.miniprojects.minijira;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<TaskListResponse> getAllTaskLists() {
        List<TaskList> taskLists = taskListDomainRepository.findAll();
        return taskLists.stream()
                .map(this::mapToTaskListResponse)
                .toList();
    }

    public Optional<TaskListResponse> findById(Long id) {
        Optional<TaskList> taskList = taskListDomainRepository.findById(id);
        return taskList.map(this::mapToTaskListResponse);
    }

    private TaskListResponse mapToTaskListResponse(TaskList taskList) {
        return new TaskListResponse(taskList.getId(), taskList.getName(), taskList.getType().name(), taskList.getStatus().name());
    }
}

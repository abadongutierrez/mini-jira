package com.jabaddon.miniprojects.minijira.infrastructure.web;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jabaddon.miniprojects.minijira.TaskListAppService;
import com.jabaddon.miniprojects.minijira.TaskListRequest;
import com.jabaddon.miniprojects.minijira.TaskListResponse;

@RestController
class TaskListResourceController {

    private final TaskListAppService taskListAppService;

    public TaskListResourceController(TaskListAppService taskListAppService) {
        this.taskListAppService = taskListAppService;
    }

    @PostMapping("/task-list")
    public ResponseEntity<Void> createTaskList(@RequestBody TaskListRequest taskListRequest) {
        Long newId = taskListAppService.createTaskList(taskListRequest);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", "/task-list/" + newId)
            .build();
    }

    @GetMapping("/task-list/{id}")
    public ResponseEntity<TaskListResponse> getTaskListById(@PathVariable Long id) {
        Optional<TaskListResponse> taskList = taskListAppService.findById(id);
        if (taskList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return taskList.map(t -> ResponseEntity.ok(t)).get();
    }
}

package com.jabaddon.miniprojects.minijira.infrastructure.web;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jabaddon.miniprojects.minijira.MiniJiraAppService;
import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;
import com.jabaddon.miniprojects.minijira.dto.TaskGroupResponse;

@CrossOrigin(origins = "*", exposedHeaders = "Location")
@RestController
class MiniJiraResourceController {

    private final MiniJiraAppService taskListAppService;

    public MiniJiraResourceController(MiniJiraAppService taskListAppService) {
        this.taskListAppService = taskListAppService;
    }

    @PostMapping("/task-groups")
    public ResponseEntity<Void> createTaskList(@RequestBody NewTaskGroupRequest taskListRequest) {
        Long newId = taskListAppService.createTaskGroup(taskListRequest);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", "/task-list/" + newId)
            .build();
    }

    @GetMapping("/task-groups/{id}")
    public ResponseEntity<TaskGroupResponse> getTaskListById(@PathVariable Long id) {
        Optional<TaskGroupResponse> taskList = taskListAppService.findById(id);
        if (taskList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return taskList.map(t -> ResponseEntity.ok(t)).get();
    }

    @GetMapping("/task-groups")
    public ResponseEntity<List<TaskGroupResponse>> getAllTaskLists() {
        // TODO implement pagination and filtering
        List<TaskGroupResponse> taskLists = taskListAppService.getAllTaskGroups();
        return ResponseEntity.ok(taskLists);
    }
}

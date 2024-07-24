package com.jabaddon.miniprojects.minijira.infrastructure.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.typemeta.funcj.control.Try;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;
import com.jabaddon.miniprojects.minijira.dto.NewTaskRequest;
import com.jabaddon.miniprojects.minijira.dto.TaskGroupResponse;
import com.jabaddon.miniprojects.minijira.dto.TaskResponse;
import com.jabaddon.miniprojects.minijira.dto.TasksInGroupResponse;
import com.jabaddon.miniprojects.minijira.errors.NotFoundException;
import com.jabaddon.miniprojects.minijira.tasks.TasksAppService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", exposedHeaders = "Location")
@RestController
@RequestMapping("/api")
class MiniJiraResourceController {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MiniJiraResourceController.class);

    private final TasksAppService taskListAppService;
    private final ObjectMapper objectMapper;

    public MiniJiraResourceController(TasksAppService taskListAppService,
            Jackson2ObjectMapperBuilder mapperBuilder) {
        this.taskListAppService = taskListAppService;
        this.objectMapper = mapperBuilder.build();
    }

    @PostMapping("/task-groups")
    public ResponseEntity<String> createTaskList(@RequestBody NewTaskGroupRequest taskListRequest) {
        Try<Long> taskGroupId = taskListAppService.createTaskGroup(taskListRequest);
        Long newId = taskGroupId.orElseThrow();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/task-groups/" + newId)
                .body("{ \"id\": " + newId + " }");
    }

    @PostMapping("/task-groups/{id}/tasks")
    public ResponseEntity<Void> createTask(@PathVariable Long id, @RequestBody @Valid NewTaskWebRequest request) {
        // TODO do we need anothehr DTO for the web request?
        Long newTaskId = taskListAppService.addTask(toNewTaskRequest(request, id)).orElseThrow();
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "/task-groups/" + id + "/tasks").build();
    }

    private NewTaskRequest toNewTaskRequest(NewTaskWebRequest request, Long id) {
        return new NewTaskRequest(request.name(), request.description(), request.estimation(), id);
    }

    @GetMapping("/task-groups/{id}/tasks")
    public ResponseEntity<TasksInGroupResponse> getTasksInGroup(@PathVariable Long id) {
        return ResponseEntity.ok(taskListAppService.getTasksInGroup(id).orElseThrow());
    }

    @DeleteMapping("/task-groups/{taskGroupId}/tasks/{taskId}")
    public void deleteTask(@PathVariable Long taskGroupId, @PathVariable Long taskId) {
        taskListAppService.deleteTaskInGroup(taskGroupId, taskId).orElseThrow();
    }

    @PutMapping("/task-groups/{taskGroupId}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> editTask(@PathVariable Long taskGroupId, @PathVariable Long taskId,
            @RequestBody @Valid NewTaskWebRequest request) {
        logger.debug(request.toString());
        return ResponseEntity.ok(
                taskListAppService.editTaskInGroup(taskGroupId, taskId, toNewTaskRequest(request, taskId))
                        .orElseThrow());
    }

    @GetMapping("/task-groups/{groupId}/tasks/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long groupId, @PathVariable Long taskId) {
        return ResponseEntity.ok(taskListAppService.getTaskById(groupId, taskId).orElseThrow());
    }

    @GetMapping("/task-groups/{id}")
    public ResponseEntity<TaskGroupResponse> getTaskListById(@PathVariable Long id) {
        Optional<TaskGroupResponse> taskList = taskListAppService.findById(id);
        return taskList.map(t -> ResponseEntity.ok(t)).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/task-groups")
    public ResponseEntity<List<TaskGroupResponse>> getAllTaskLists() {
        // TODO implement pagination and filtering
        List<TaskGroupResponse> taskLists = taskListAppService.getAllTaskGroups();
        return ResponseEntity.ok(taskLists);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        logger.error("Error processing request", e);
        return switch (e) {
            case IllegalArgumentException _ ->
                ResponseEntity.status(HttpStatus.BAD_REQUEST).body(formatExceptionToJson(e));
            case NotFoundException _ -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(formatExceptionToJson(e));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(formatExceptionToJson(e));
        };
    }

    private String formatExceptionToJson(Exception e) {
        try {
            Map<String, String> of = new HashMap<>();
            of.put("message", e.getMessage());
            of.put("type", e.getClass().getSimpleName());
            return objectMapper.writeValueAsString(of);
        } catch (JsonProcessingException e1) {
            return "{ \"message\": \"Error formatting exception\" }";
        }
    }
}

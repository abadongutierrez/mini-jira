package com.jabaddon.miniprojects.minijira.infrastructure.web;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;
import com.jabaddon.miniprojects.minijira.dto.NewTaskRequest;
import com.jabaddon.miniprojects.minijira.dto.TaskGroupResponse;
import com.jabaddon.miniprojects.minijira.dto.TaskResponse;
import com.jabaddon.miniprojects.minijira.errors.NotFoundException;
import com.jabaddon.miniprojects.minijira.tasks.TasksAppService;

@Controller
public class PageController {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PageController.class);


    private final TasksAppService taskListAppService;

    public PageController(TasksAppService taskListAppService) {
        this.taskListAppService = taskListAppService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/task-groups")
    public String taskGroups(Model model) {
        java.util.List<TaskGroupResponse> taskLists = taskListAppService.getAllTaskGroups();
        AtomicBoolean active = new AtomicBoolean(true);
        List<TaskGroupUIItem> list = taskLists.stream().map((taskGroupResponse) -> {
            TaskGroupUIItem item = new TaskGroupUIItem(active.get(), taskGroupResponse);
            active.set(false);
            return item;
        }).toList();
        model.addAttribute("taskGroups", list);
        return "task-groups/index";
    }

    @PostMapping("/task-groups")
    public String createTaskGroup(@ModelAttribute NewTaskGroupRequest request, Model model) {        
        try {
            taskListAppService.createTaskGroup(request).orElseThrow();            
            return "redirect:/task-groups";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error?", true);
            model.addAttribute("error", e.getMessage());
            return "task-groups/new";
        }
    }

    @PostMapping("/task-groups/{id}/tasks")
    public String createTaskInGroup(@PathVariable Long id, @ModelAttribute NewTaskWebRequest request, Model model) {
        try {
            taskListAppService.addTask(toNewTaskRequest(request, id)).orElseThrow();
            return "redirect:/task-groups";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error?", true);
            model.addAttribute("error", e.getMessage());
            return "task-groups/new-task";
        }
    }

    @PostMapping("/task-groups/{groupId}/tasks/{taskId}")
    public String updateTask(@PathVariable Long groupId, @PathVariable Long taskId,
        @ModelAttribute NewTaskWebRequest request, Model model) {
        try {
            taskListAppService.editTaskInGroup(groupId, taskId, toNewTaskRequest(request, taskId)).orElseThrow();
            return "redirect:/task-groups";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error?", true);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("taskGroup", taskListAppService.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Task Group not found")));
            model.addAttribute("task", taskListAppService.getTaskById(groupId, taskId).orElseThrow());
            return "task-groups/edit-task";
        }
    }

    private NewTaskRequest toNewTaskRequest(NewTaskWebRequest request, Long id) {
        return new NewTaskRequest(request.name(), request.description(), request.estimation(), id);
    }

    @GetMapping("/task-groups/new")
    public String newTaskGroup(Model model) {
        model.addAttribute("error?", false);
        return "task-groups/new";
    }

    @GetMapping("/task-groups/{id}/tasks/new")
    public String newTaskInGroup(@PathVariable Long id, Model model) {
        Optional<TaskGroupResponse> taskList = taskListAppService.findById(id);
        model.addAttribute("error?", false);
        model.addAttribute("taskGroup", taskList.get());
        return "task-groups/new-task";
    }

    @GetMapping("/task-groups/{groupId}/tasks/{taskId}/edit")
    public String editTask(@PathVariable Long groupId, @PathVariable Long taskId, Model model) {
        model.addAttribute("error?", false);
        model.addAttribute("taskGroup", taskListAppService.findById(groupId)
            .orElseThrow(() -> new NotFoundException("Task Group not found")));
        TaskResponse taskResponse = taskListAppService.getTaskById(groupId, taskId).orElseThrow();
        logger.debug(taskResponse.toString());
        model.addAttribute("task", taskResponse);
        return "task-groups/edit-task";
    }

    // TODO: implement exception handling
}

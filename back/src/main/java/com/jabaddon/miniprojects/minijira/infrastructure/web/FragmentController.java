package com.jabaddon.miniprojects.minijira.infrastructure.web;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jabaddon.miniprojects.minijira.MiniJiraAppService;
import com.jabaddon.miniprojects.minijira.dto.TaskGroupResponse;
import com.jabaddon.miniprojects.minijira.dto.TasksInGroupResponse;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/fragments")
public class FragmentController {
    
    private final MiniJiraAppService taskListAppService;

    public FragmentController(MiniJiraAppService taskListAppService) {
        this.taskListAppService = taskListAppService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/task-groups/{id}/tasks")
    public String tasksInGroup(@PathVariable Long id, Model model) {
        TasksInGroupResponse tasks = taskListAppService.getTasksInGroup(id).orElseThrow();
        model.addAttribute("taskGroupId", id);
        model.addAttribute("tasks", tasks.tasks());
        model.addAttribute("tasks?", !tasks.tasks().isEmpty());
        return "task-groups/fragments/tasks-in-group";
    }

    @GetMapping("/task-groups/{id}/total-estimation")
    public String totalEstimation(@PathVariable Long id, Model model) {
        Optional<TaskGroupResponse> taskList = taskListAppService.findById(id);
        model.addAttribute("taskGroupId", id);
        model.addAttribute("totalEstimation", taskList.get().totalEstimation());
        return "task-groups/fragments/total-estimation";
    }

    @DeleteMapping("/task-groups/{id}/tasks/{taskId}")
    public void deleteTask(@PathVariable Long id, @PathVariable Long taskId, HttpServletResponse response) {
        taskListAppService.deleteTaskInGroup(id, taskId).orElseThrow();
        response.addHeader("HX-Trigger-After-Swap",
            List.of("update-total-estimation-id-" + id, "reload-tasks-id-" + id).stream().collect(Collectors.joining(", ")));
    }
}

class TaskGroupUIItem {
    public boolean active;
    public TaskGroupResponse taskGroup;

    public TaskGroupUIItem(boolean active, TaskGroupResponse taskGroup) {
        this.active = active;
        this.taskGroup = taskGroup;
    }
}

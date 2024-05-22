package com.jabaddon.miniprojects.minijira;

import java.time.LocalDateTime;

class TaskList {
    private Long id;
    private final String name;
    private final TaskListType type;
    private LocalDateTime beginsAt;
    private LocalDateTime endsAt;
    private final TaskListStatus status;

    public TaskList(String name, TaskListType type) {
        this.name = name;
        this.type = type;
        this.status = TaskListStatus.NOT_STARTED;
    }

    public TaskList(Long id, String name, TaskListType type, TaskListStatus status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TaskListType getType() {
        return type;
    }

    public TaskListStatus getStatus() {
        return status;
    }
}

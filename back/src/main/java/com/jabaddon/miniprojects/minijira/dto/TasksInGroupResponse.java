package com.jabaddon.miniprojects.minijira.dto;

import java.util.List;

public record TasksInGroupResponse(List<TaskResponse> tasks) {

    public static final TasksInGroupResponse EMPTY = new TasksInGroupResponse(List.of());
}

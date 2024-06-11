package com.jabaddon.miniprojects.minijira.infrastructure.web;

import jakarta.validation.constraints.NotNull;

public record NewTaskWebRequest(
    @NotNull(message = "Task Name is required")
    String name,
    String description,
    Double estimation) {
    
}

package com.jabaddon.miniprojects.minijira.dto;

public record NewTaskRequest(String name, String description, Double estimation, Long taskGroupId) {

}

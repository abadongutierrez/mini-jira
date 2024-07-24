package com.jabaddon.miniprojects.minijira.tasks;

interface Task {
    Long getId();
    String getName();
    String getDescription();
    Double getEstimation();
}

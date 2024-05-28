package com.jabaddon.miniprojects.minijira;

import java.time.LocalDateTime;

public interface Iteration {
    Long getId();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    TaskGroup getTaskGroup();
    Double totalEstimation();
}

package com.jabaddon.miniprojects.minijira.iterations;

import java.time.LocalDateTime;

public interface Iteration {
    Long getId();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    Long getTaskGroup();
    Double totalEstimation();
}

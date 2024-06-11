package com.jabaddon.miniprojects.minijira;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.typemeta.funcj.control.Either;

import lombok.Getter;

// Aggregate
class TaskGroup {
    private Long id;
    private final String name;
    private final TaskGroupStatus status;
    private final List<Task> taskList = new ArrayList<>();

    TaskGroup(String name) {
        this.name = name;
        this.status = TaskGroupStatus.NOT_STARTED;
    }

    TaskGroup(Long id, String name, TaskGroupStatus status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TaskGroupStatus getStatus() {
        return status;
    }

    public List<Task> getTasks() {
        return Collections.unmodifiableList(taskList);
    }

    public Either<Exception, Boolean> addTask(String name, String description, Double estimation) {
        return validateEstimation(estimation)
            .map(_ -> {
                InnerTask newTask = new InnerTask(name, description, estimation);
                taskList.add(newTask);
                return true;
            });
    }

    private Either<Exception, Double> validateEstimation(Double estimation) {
        if (estimation != null && estimation <= 0) {
            return Either.left(new IllegalArgumentException("Estimation must be greater than 0"));
        }
        // WARNING returning 0.0 because cannot return null, anyway this value is ignored
        return Either.right(0.0);
    }

    public void addTask(Long id, String name, String description, Double estimation) {
        InnerTask newTask = new InnerTask(id, name, description, estimation);
        taskList.add(newTask);
    }

    public Iteration startIteration(LocalDateTime startAt, LocalDateTime endAt) {
        InnerIteration iteration = new InnerIteration(this, startAt, endAt);
        iteration.createInitialCommitment();
        return iteration;
    }

    @Getter
    private class InnerTask implements Task {
        private Long id;
        private final String name;
        private final String description;
        private final Double estimation;
    
        private InnerTask(String name, String description, Double estimation) {
            this.name = name;
            this.description = description;
            this.estimation = estimation;
        }

        private InnerTask(Long id, String name, String description, Double estimation) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.estimation = estimation;
        }
    }

    @Getter
    private class InnerIteration implements Iteration {
        private Long id;
        private final TaskGroup taskGroup;
        private final LocalDateTime startDate;
        private final LocalDateTime endDate;

        public InnerIteration(TaskGroup taskGroup, LocalDateTime startAt, LocalDateTime endAt) {
            this.taskGroup = taskGroup;
            this.startDate = startAt;
            this.endDate = endAt;
        }

        public void createInitialCommitment() {
            taskGroup.getTasks().forEach(task -> {
                // create commitment
                InnerTaskCommitment commitment = new InnerTaskCommitment(task.getId(), task.getEstimation());
            });
        }

        @Override
        public Double totalEstimation() {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'totalEstimation'");
        }
    }

    @Getter
    private class InnerTaskCommitment {
        private Long id;
        private final Long taskId;
        private final Double estimation;

        public InnerTaskCommitment(Long taskId, Double estimation) {
            this.taskId = taskId;
            this.estimation = estimation;
        }
    }

    
}

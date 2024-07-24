package com.jabaddon.miniprojects.minijira.iterations;

import java.time.LocalDateTime;

import lombok.Getter;

// Aggregate
class IterationAggregate {
    private Long id;
    private final Long taskGroup;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public IterationAggregate(Long taskGroup, LocalDateTime startAt, LocalDateTime endAt) {
        this.taskGroup = taskGroup;
        this.startDate = startAt;
        this.endDate = endAt;
    }

    public void createInitialCommitment() {
        // taskGroup.getTasks().forEach(task -> {
        // // create commitment
        // InnerTaskCommitment commitment = new InnerTaskCommitment(task.getId(),
        // task.getEstimation());
        // });
    }

    public Double totalEstimation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'totalEstimation'");
    }

    // TODO: Task Group should be in charge of creating iterations??
    public Iteration startIteration(LocalDateTime startAt, LocalDateTime endAt) {
        InnerIteration iteration = new InnerIteration(taskGroup, startAt, endAt);
        iteration.createInitialCommitment();
        return iteration;
    }

    @Getter
    private class InnerIteration implements Iteration {
        private Long id;
        private final Long taskGroup;
        private final LocalDateTime startDate;
        private final LocalDateTime endDate;

        public InnerIteration(Long taskGroup, LocalDateTime startAt, LocalDateTime endAt) {
            this.taskGroup = taskGroup;
            this.startDate = startAt;
            this.endDate = endAt;
        }

        public void createInitialCommitment() {
            // taskGroup.getTasks().forEach(task -> {
            // // create commitment
            // InnerTaskCommitment commitment = new InnerTaskCommitment(task.getId(),
            // task.getEstimation());
            // });
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
        // private final TaskCommitmentStatus status;

        public InnerTaskCommitment(Long taskId, Double estimation) {
            this.taskId = taskId;
            this.estimation = estimation;
        }
    }
}

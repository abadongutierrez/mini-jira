package com.jabaddon.miniprojects.minijira.iterations;

class DomainRepositoryImpl implements  DomainRepository {

    private final IterationJpaRepository iterationJpaRepository;

    DomainRepositoryImpl(com.jabaddon.miniprojects.minijira.iterations.IterationJpaRepository iterationJpaRepository) {
        this.iterationJpaRepository = iterationJpaRepository;
    }

    @Override
    public Long saveNewIteration(Iteration iteration) {
        // IterationJpaEntity iterationJpaEntity = new IterationJpaEntity();
        // iterationJpaEntity.startAt = iteration.getStartDate();
        // iterationJpaEntity.endAt = iteration.getEndDate();
        // iterationJpaEntity.taskGroupId = iteration.getTaskGroup().getId();
        // IterationJpaEntity newIteration = iterationJpaRepository.save(iterationJpaEntity);
        // return newIteration.id;
        return null;
    }
}

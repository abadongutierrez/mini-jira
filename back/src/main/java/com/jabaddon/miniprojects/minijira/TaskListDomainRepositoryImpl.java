package com.jabaddon.miniprojects.minijira;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
class TaskListDomainRepositoryImpl implements TaskListDomainRepository {

    private final TaskListJpaRepository taskListJpaRepository;

    public TaskListDomainRepositoryImpl(TaskListJpaRepository taskListJpaRepository) {
        this.taskListJpaRepository = taskListJpaRepository;
    }

    @Override
    public Long save(TaskList taskList) {
        TaskListJpaEntity taskListJpaEntity = new TaskListJpaEntity();
        taskListJpaEntity.name = taskList.getName();
        taskListJpaEntity.type = taskList.getType().name();
        taskListJpaEntity.status = taskList.getStatus().name();
        TaskListJpaEntity persisted = taskListJpaRepository.save(taskListJpaEntity);
        return persisted.id;
    }

    @Override
    public Optional<TaskList> findById(Long id) {
        Optional<TaskListJpaEntity> taskListJpaEntity = taskListJpaRepository.findById(id);
        return taskListJpaEntity.map(this::mapToDomainEntity);
    }

    @Override
    public List<TaskList> findAll() {
        List<TaskListJpaEntity> taskListJpaEntities = taskListJpaRepository.findAll();
        return taskListJpaEntities.stream()
                .map(this::mapToDomainEntity)
                .toList();
    }

    private TaskList mapToDomainEntity(TaskListJpaEntity taskListJpaEntity) {
        return new TaskList(
                taskListJpaEntity.id,
                taskListJpaEntity.name,
                TaskListType.valueOf(taskListJpaEntity.type),
                TaskListStatus.valueOf(taskListJpaEntity.status));
    }
}

package com.jabaddon.miniprojects.minijira;

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
        return taskListJpaEntity.map(
                t -> new TaskList(t.id,
                    t.name,
                    TaskListType.valueOf(t.type),
                    TaskListStatus.valueOf(t.status)));
    }

}

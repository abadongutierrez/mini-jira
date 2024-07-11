package com.jabaddon.miniprojects.minijira;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class TaskGroupDomainRepositoryImpl implements TaskGroupDomainRepository {

    private final TaskGroupJpaRepository taskGroupJpaRepository;
    private final TaskJpaRepository taskJpaRepository;
    private final IterationJpaRepository iterationJpaRepository;

    public TaskGroupDomainRepositoryImpl(TaskGroupJpaRepository taskListJpaRepository, TaskJpaRepository taskJpaRepository, com.jabaddon.miniprojects.minijira.IterationJpaRepository iterationJpaRepository) {
        this.taskGroupJpaRepository = taskListJpaRepository;
        this.taskJpaRepository = taskJpaRepository;
        this.iterationJpaRepository = iterationJpaRepository;
    }

    @Override
    public Long save(TaskGroup taskList) {
        TaskGroupJpaEntity taskListJpaEntity = new TaskGroupJpaEntity();
        taskListJpaEntity.name = taskList.getName();
        taskListJpaEntity.status = taskList.getStatus().name();
        TaskGroupJpaEntity persisted = taskGroupJpaRepository.save(taskListJpaEntity);
        return persisted.id;
    }

    @Transactional
    @Override
    public Optional<TaskGroup> findById(Long id) {
        Optional<TaskGroupJpaEntity> taskListJpaEntity = taskGroupJpaRepository.findById(id);
        return taskListJpaEntity.map(this::mapToDomainEntity);
    }

    @Override
    public List<TaskGroup> findAll() {
        List<TaskGroupJpaEntity> taskListJpaEntities = taskGroupJpaRepository.findAll();
        return taskListJpaEntities.stream()
                .map(this::mapToDomainEntity)
                .toList();
    }

    @Transactional
    @Override
    public void saveNewTasks(TaskGroup taskGroup) {
        taskGroupJpaRepository.findById(taskGroup.getId())
                .ifPresent(taskGroupJpaEntity -> {
                    List<Task> tasks = taskGroup.getTasks();
                    Stream<Task> taskWithIdNull = tasks.stream().filter(t -> t.getId() == null);
                    List<TaskJpaEntity> newTasks = taskWithIdNull
                        .map(t -> mapTaskToTaskJpaEntity(taskGroupJpaEntity, t)).toList();
                    taskJpaRepository.saveAll(newTasks);
                });
    }

    private TaskJpaEntity mapTaskToTaskJpaEntity(TaskGroupJpaEntity taskGroupJpaEntity, Task t) {
        TaskJpaEntity taskJpaEntity = new TaskJpaEntity();
        taskJpaEntity.name = t.getName();
        taskJpaEntity.description = t.getDescription();
        taskJpaEntity.estimation = t.getEstimation();
        taskJpaEntity.taskGroupId = taskGroupJpaEntity.id;
        return taskJpaEntity;
    }

    private TaskJpaEntity mapTaskToEditTaskJpaEntity(Long groupId, Task t) {
        TaskJpaEntity taskJpaEntity = new TaskJpaEntity();
        taskJpaEntity.id = t.getId();
        taskJpaEntity.name = t.getName();
        taskJpaEntity.description = t.getDescription();
        taskJpaEntity.estimation = t.getEstimation();
        taskJpaEntity.taskGroupId = groupId;
        return taskJpaEntity;
    }

    private TaskGroup mapToDomainEntity(TaskGroupJpaEntity taskListJpaEntity) {
        TaskGroup tg = new TaskGroup(
                taskListJpaEntity.id,
                taskListJpaEntity.name,
                TaskGroupStatus.valueOf(taskListJpaEntity.status));
        taskJpaRepository.findByTaskGroupId(taskListJpaEntity.id)
            .forEach(t -> tg.addTask(t.id, t.name, t.description, t.estimation));
        return tg;
    }

    @Override
    public Long saveNewIteration(Iteration iteration) {
        IterationJpaEntity iterationJpaEntity = new IterationJpaEntity();
        iterationJpaEntity.startAt = iteration.getStartDate();
        iterationJpaEntity.endAt = iteration.getEndDate();
        iterationJpaEntity.taskGroupId = iteration.getTaskGroup().getId();
        IterationJpaEntity newIteration = iterationJpaRepository.save(iterationJpaEntity);
        return newIteration.id;
    }

    @Override
    public Optional<TaskGroup> findByName(String name) {
        Optional<TaskGroupJpaEntity> taskListJpaEntity = taskGroupJpaRepository.findByName(name);
        return taskListJpaEntity.map(this::mapToDomainEntity);
    }

    @Override
    public boolean existsTaskByName(String name) {
        return !taskJpaRepository.findByName(name).isEmpty();
    }

    @Override
    public void deleteTasks(TaskGroup taskGroup) {
        List<Long> ids = taskGroup.getTaskToDelete().stream().map(Task::getId).toList();
        taskJpaRepository.deleteAllById(ids);
        taskGroup.resetTaskToDelete();
    }

    @Override
    public void saveEditedTasks(TaskGroup taskGroup) {
        List<Task> tasks = taskGroup.getTasksToEdit();
        List<TaskJpaEntity> editedTasks = tasks.stream().map(t -> mapTaskToEditTaskJpaEntity(taskGroup.getId(), t)).toList();
        taskJpaRepository.saveAll(editedTasks);
    }
}

package com.jabaddon.miniprojects.minijira.iterations;

import java.time.LocalDateTime;

import org.typemeta.funcj.control.Try;

public class IterationsAppService {
    
    public Try<Long> startIteration(Long groupId, LocalDateTime now, LocalDateTime plusDays) {
        // Optional<TaskGroup> taskGroup = taskGroupDomainRepository.findById(groupId);
        // if (taskGroup.isEmpty()) {
        //     return Try.failure(new NotFoundException("Task Group not found"));
        // }
        // TaskGroup tp = taskGroup.get();
        // Iteration iteration = tp.startIteration(now, plusDays);
        // return taskGroupDomainRepository.saveNewIteration(iteration);
        return null;
    }
}

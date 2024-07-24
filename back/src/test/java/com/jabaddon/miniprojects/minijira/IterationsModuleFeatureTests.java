package com.jabaddon.miniprojects.minijira;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.github.javafaker.Faker;
import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;
import com.jabaddon.miniprojects.minijira.dto.NewTaskRequest;
import com.jabaddon.miniprojects.minijira.iterations.IterationsAppService;
import com.jabaddon.miniprojects.minijira.tasks.TasksAppService;

public class IterationsModuleFeatureTests {

    private Faker faker = new Faker();

    @org.testcontainers.junit.jupiter.Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("integration-tests-db")
            .withUsername("username")
            .withPassword("password");

    @Autowired
    private IterationsAppService iterationsAppService;

    @Autowired
    private TasksAppService tasksAppService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void startAnIteration() {
        Long groupId = createTaskGroupWithTasks();

        Long newIterationId = iterationsAppService.startIteration(groupId, LocalDateTime.now(), LocalDateTime.now().plusDays(7)).orElseThrow();
        assertThat(newIterationId).isNotNull();
        assertThat(newIterationId).isGreaterThan(0);
    }

    private Long createTaskGroupWithTasks() {
        Long groupId = tasksAppService.createTaskGroup(new NewTaskGroupRequest("Sprint 1 " + faker.pokemon().name())).orElseThrow();
        tasksAppService.addTask(new NewTaskRequest("Task " + randomNum(), "Task 1 description", 3.0, groupId));
        tasksAppService.addTask(new NewTaskRequest("Task " + randomNum(), "Task 2 description", 1.0, groupId));
        tasksAppService.addTask(new NewTaskRequest("Task " + randomNum(), "Task 3 description", 2.5, groupId));
        return groupId;
    }

    private int randomNum() {
        return faker.random().nextInt(1000);
    }
}

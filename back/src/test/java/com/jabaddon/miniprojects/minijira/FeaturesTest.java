package com.jabaddon.miniprojects.minijira;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;
import com.jabaddon.miniprojects.minijira.dto.NewTaskRequest;
import com.jabaddon.miniprojects.minijira.dto.TasksInGroupResponse;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
public class FeaturesTest {

    @org.testcontainers.junit.jupiter.Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("integration-tests-db")
            .withUsername("username")
            .withPassword("password");

    @Autowired
    private MiniJiraAppService appService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }
    
    @Test
    void createTaskGroup() {
        Long id = appService.createTaskGroup(new NewTaskGroupRequest("Task Group 1", "BACKLOG"));

        assertThat(id).isNotNull();
        assertThat(id).isGreaterThan(0);

        appService.addTask(new NewTaskRequest("Task 1", "Task 1 description", null, id));
        appService.addTask(new NewTaskRequest("Task 2", "Task 2 description", 1.0, id));
        appService.addTask(new NewTaskRequest("Task 3", "Task 3 description", 2.5, id));

        Map<String, Object> map = jdbcTemplate.queryForMap("select count(*) as count from tasks where task_group_id = ?", new Object[]{ id });
        assertThat(map.get("count")).isEqualTo(3L);

        TasksInGroupResponse tasks = appService.getTasksInGroup(id);

        assertThat(tasks.tasks()).size().isEqualTo(3);
    }


    @Test
    void startAnIteration() {
        Long groupId = appService.createTaskGroup(new NewTaskGroupRequest("Sprint 1", "SPRINT"));
        appService.addTask(new NewTaskRequest("Task 1", "Task 1 description", 3.0, groupId));
        appService.addTask(new NewTaskRequest("Task 2", "Task 2 description", 1.0, groupId));
        appService.addTask(new NewTaskRequest("Task 3", "Task 3 description", 2.5, groupId));

        Long newIterationId = appService.startIteration(groupId, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        assertThat(newIterationId).isNotNull();
        assertThat(newIterationId).isGreaterThan(0);
    }
}

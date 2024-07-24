package com.jabaddon.miniprojects.minijira;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.typemeta.funcj.control.Try;

import com.github.javafaker.Faker;
import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;
import com.jabaddon.miniprojects.minijira.dto.NewTaskRequest;
import com.jabaddon.miniprojects.minijira.dto.TasksInGroupResponse;
import com.jabaddon.miniprojects.minijira.errors.NotFoundException;
import com.jabaddon.miniprojects.minijira.tasks.TasksAppService;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Testcontainers
public class TasksModuleFeaturesTest {

    private Faker faker = new Faker();

    @org.testcontainers.junit.jupiter.Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("integration-tests-db")
            .withUsername("username")
            .withPassword("password");

    @Autowired
    private TasksAppService appService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Test
    void taskGroupNameShouldIncludeName() {
        Try<Long> taskGroup = appService.createTaskGroup(new NewTaskGroupRequest(" "));
        assertThat(taskGroup.isSuccess()).isFalse();
        assertThrows(IllegalArgumentException.class, () -> taskGroup.orElseThrow());
    }

    @Test
    void taskGroupNameSouldBeUnique() {
        int number = randomNum();
        String name = "Task Group " + number;
        appService.createTaskGroup(new NewTaskGroupRequest(name));
        Try<Long> taskGroup =
            appService.createTaskGroup(new NewTaskGroupRequest(name));
        assertThat(taskGroup.isSuccess()).isFalse();
        assertThrows(IllegalArgumentException.class, () -> taskGroup.orElseThrow());
    }

    private int randomNum() {
        return faker.random().nextInt(1000);
    }

    @Test
    void createTaskWithRepeatedName() {
        Long id = appService.createTaskGroup(new NewTaskGroupRequest("Task Group " + randomNum())).orElseThrow();

        int taskNumber = randomNum();
        Try<Long> firstTask = appService.addTask(new NewTaskRequest("Task " + taskNumber, "Task 1 description", null, id));
        assertThat(firstTask.isSuccess()).isTrue();

        Try<Long> secondTask = appService.addTask(new NewTaskRequest("Task " + taskNumber, "Task 2 description", 1.0, id));
        assertThat(secondTask.isSuccess()).isFalse();
        assertThrows(IllegalArgumentException.class, () -> secondTask.orElseThrow());
    }
    
    @Test
    void createTaskGroup() {
        Long id = appService.createTaskGroup(new NewTaskGroupRequest("Task Group " + faker.pokemon().name())).orElseThrow();

        assertThat(id).isNotNull();
        assertThat(id).isGreaterThan(0);

        appService.addTask(new NewTaskRequest("Task " + randomNum(), "Task 1 description", null, id));
        appService.addTask(new NewTaskRequest("Task " + randomNum(), "Task 2 description", 1.0, id));
        appService.addTask(new NewTaskRequest("Task " + randomNum(), "Task 3 description", 2.5, id));

        Map<String, Object> map = jdbcTemplate.queryForMap("select count(*) as count from tasks where task_group_id = ?", new Object[]{ id });
        assertThat(map.get("count")).isEqualTo(3L);

        TasksInGroupResponse tasks = appService.getTasksInGroup(id).orElseThrow();

        assertThat(tasks.tasks()).size().isEqualTo(3);
    }

    @Test
    void createTaskInNotExistingTaskGroup() {
        Try<Long> result =
            appService.addTask(new NewTaskRequest("Task 1", "Task 1 description", null, 1000L));
        assertThat(result.isSuccess()).isFalse();
        assertThrows(NotFoundException.class, () -> result.orElseThrow());
    }
}

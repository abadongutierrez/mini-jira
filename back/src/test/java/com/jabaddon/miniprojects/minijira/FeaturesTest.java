package com.jabaddon.miniprojects.minijira;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.typemeta.funcj.control.Either;

import com.jabaddon.miniprojects.minijira.dto.NewTaskGroupRequest;
import com.jabaddon.miniprojects.minijira.dto.NewTaskRequest;
import com.jabaddon.miniprojects.minijira.dto.TasksInGroupResponse;
import com.jabaddon.miniprojects.minijira.errors.NotFoundException;

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
    void taskGroupNameShouldIncludeName() {
        Either<Exception, Long> taskGroup = appService.createTaskGroup(new NewTaskGroupRequest(" "));
        assertThat(taskGroup.isLeft()).isTrue();
        assertThat(taskGroup.left()).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void taskGroupNameSouldBeUnique() {
        int number = getRandomNumber();
        String name = "Task Group " + number;
        appService.createTaskGroup(new NewTaskGroupRequest(name));
        Either<Exception, Long> taskGroup =
            appService.createTaskGroup(new NewTaskGroupRequest(name));
        assertThat(taskGroup.isLeft()).isTrue();
        assertThat(taskGroup.left()).isInstanceOf(IllegalArgumentException.class);
    }

    private int getRandomNumber() {
        Random rand = new Random();
        int number = rand.nextInt(1000);
        return number;
    }

    @Test
    void createTaskWithRepeatedName() {
        Long id = appService.createTaskGroup(new NewTaskGroupRequest("Task Group " + getRandomNumber())).right();

        int taskNumber = getRandomNumber();
        Either<Exception, Long> firstTask = appService.addTask(new NewTaskRequest("Task " + taskNumber, "Task 1 description", null, id));
        assertThat(firstTask.isRight()).isTrue();

        Either<Exception, Long> secondTask = appService.addTask(new NewTaskRequest("Task " + taskNumber, "Task 2 description", 1.0, id));
        assertThat(secondTask.isLeft()).isTrue();
        assertThat(secondTask.left()).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    void createTaskGroup() {
        Long id = appService.createTaskGroup(new NewTaskGroupRequest("Task Group " + getRandomNumber())).right();

        assertThat(id).isNotNull();
        assertThat(id).isGreaterThan(0);

        appService.addTask(new NewTaskRequest("Task " + getRandomNumber(), "Task 1 description", null, id));
        appService.addTask(new NewTaskRequest("Task " + getRandomNumber(), "Task 2 description", 1.0, id));
        appService.addTask(new NewTaskRequest("Task " + getRandomNumber(), "Task 3 description", 2.5, id));

        Map<String, Object> map = jdbcTemplate.queryForMap("select count(*) as count from tasks where task_group_id = ?", new Object[]{ id });
        assertThat(map.get("count")).isEqualTo(3L);

        TasksInGroupResponse tasks = appService.getTasksInGroup(id).right();

        assertThat(tasks.tasks()).size().isEqualTo(3);
    }

    @Test
    void createTaskInNotExistingTaskGroup() {
        Either<Exception, Long> result =
            appService.addTask(new NewTaskRequest("Task 1", "Task 1 description", null, 1000L));
        assertThat(result.isLeft()).isTrue();
        assertThat(result.left()).isInstanceOf(NotFoundException.class);
    }


    @Test
    void startAnIteration() {
        Long groupId = appService.createTaskGroup(new NewTaskGroupRequest("Sprint 1")).right();
        appService.addTask(new NewTaskRequest("Task " + getRandomNumber(), "Task 1 description", 3.0, groupId));
        appService.addTask(new NewTaskRequest("Task " + getRandomNumber(), "Task 2 description", 1.0, groupId));
        appService.addTask(new NewTaskRequest("Task " + getRandomNumber(), "Task 3 description", 2.5, groupId));

        Map<String, Object> map = jdbcTemplate.queryForMap("select count(*) as count from tasks where task_group_id = ?", new Object[]{ groupId });
        assertThat(map.get("count")).isEqualTo(3L);

        Long newIterationId = appService.startIteration(groupId, LocalDateTime.now(), LocalDateTime.now().plusDays(7));
        assertThat(newIterationId).isNotNull();
        assertThat(newIterationId).isGreaterThan(0);
    }
}

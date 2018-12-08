package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.production.dto.TaskDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Category;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.TaskRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupTasks();
    }

    @Test
    public void readAllTasksTest() {
        ResponseEntity<TaskDTO[]> forEntity = restTemplate.exchange("/tasks", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), TaskDTO[].class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<TaskDTO> taskDTOs = Arrays.asList(forEntity.getBody());
        taskRequests.forEach(request -> taskDTOs.stream()
                .anyMatch(taskDTO -> checkIfTaskDtoAndRequestMatch(taskDTO, request))
        );
    }

    @Test
    public void readOneTaskTest() {
        for (int i = 0; i < taskRequests.size(); i++) {
            ResponseEntity<TaskDTO> exchange = restTemplate.exchange("/tasks/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), TaskDTO.class, i + 1);
            assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(exchange.getBody()).isNotNull();
        }
    }

    @Test
    public void addOneTaskTest() {
        TaskRequest oneTaskRequestWithAssigneeId = getOneTaskRequestWithAssigneeId(2);
        ResponseEntity<TaskDTO> taskDTOResponseEntity = restTemplate.postForEntity("/tasks",
                new HttpEntity<>(oneTaskRequestWithAssigneeId, requestHeaders), TaskDTO.class);
        assertThat(taskDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertTrue(checkIfTaskDtoAndRequestMatch(taskDTOResponseEntity.getBody(), oneTaskRequestWithAssigneeId));
    }

    @Test
    public void updateTaskCategoryTest() {
        ResponseEntity<TaskDTO> exchange = restTemplate.exchange("/tasks/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), TaskDTO.class, 1);
        assertThat(exchange.getBody().getCategory()).isEqualTo(Category.TO_DO);

        ResponseEntity<TaskDTO> taskResponseEntity = restTemplate.exchange("/tasks/{id}",
                HttpMethod.PUT, new HttpEntity<>(Category.DOING.name(), requestHeaders), TaskDTO.class, 1);
        assertThat(taskResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(taskResponseEntity.getBody().getCategory()).isEqualTo(Category.DOING);
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> exchange = restTemplate.exchange("/tasks/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private boolean checkIfTaskDtoAndRequestMatch(TaskDTO task, TaskRequest request) {
        return request.getName().equals(task.getName()) &&
                request.getPrecedingTaskIds().size() == task.getPrecedingTaskIds().size() &&
                request.getEstimatedTime().equals(task.getEstimatedTime());
    }
}

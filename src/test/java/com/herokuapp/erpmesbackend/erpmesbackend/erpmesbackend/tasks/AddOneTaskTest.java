package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.tasks;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Category;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Task;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.TaskRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddOneTaskTest extends FillBaseTemplate {

    private Task task;

    @Before
    public void init() {
        setupToken();
        addOneAdminRequest(true);
        addOneOrderRequest(true);
        addTaskRequests(true);

        String name = taskFactory.generateName();

        Employee assignee = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee.class, 1).getBody();
        Long assigneeId = assignee.getId();

        List<Task> precedingTasks = new ArrayList<>();
        List<Long> precedingTaskIds = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            Task precedingTask = restTemplate.exchange("/tasks/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Task.class, i).getBody();
            precedingTaskIds.add(precedingTask.getId());
            precedingTasks.add(precedingTask);
        }

        String details = taskFactory.generateDetails();
        int estimatedTimeInMinutes = taskFactory.generateEstimatedTimeInMinutes();
        LocalDateTime deadline = taskFactory.generateDeadline();

        Type type = Type.ORDER;
        Long reference = 1L;

        LocalDateTime scheduledTime = taskFactory.generateScheduledTime();

        taskRequest = new TaskRequest(name, assigneeId, precedingTaskIds, details, estimatedTimeInMinutes,
                deadline, null, null, scheduledTime);
        task = new Task(name, Category.TODO, assignee, precedingTasks, details, estimatedTimeInMinutes, deadline,
                type, reference, scheduledTime);
    }

    @Test
    public void checkIfResponseContainsAddedTask() {
        ResponseEntity<Task> taskResponseEntity = restTemplate.postForEntity("/tasks",
                new HttpEntity<>(taskRequest, requestHeaders),  Task.class);
        assertThat(taskResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Task body = taskResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(task));
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.tasks;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.teams.TeamRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.Category;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.Task;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.TaskRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
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
        addOneAdminRequest(true);
        addTaskRequests(true);

        String name = taskFactory.generateName();
        Category category = taskFactory.generateTodoCategory();
        Employee assignee = restTemplate.getForEntity("/employees/{id}", Employee.class, 1).getBody();
        Long assigneeId = assignee.getId();

        List<Task> precedingTasks = new ArrayList<>();
        List<Long> precedingTaskIds = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            Task precedingTask = restTemplate.getForEntity("/tasks/{id}", Task.class, i).getBody();
            precedingTaskIds.add(precedingTask.getId());
            precedingTasks.add(precedingTask);
        }

        String details = taskFactory.generateDetails();
        int estimatedTimeInMinutes = taskFactory.generateEstimatedTimeInMinutes();
        LocalDateTime deadline = taskFactory.generateDeadline();

        taskRequest = new TaskRequest(name, category, assigneeId, precedingTaskIds, details, estimatedTimeInMinutes, deadline);
        task = new Task(name, category, assignee, precedingTasks, details, estimatedTimeInMinutes, deadline);
    }

    @Test
    public void checkIfResponseContainsAddedTask() {
        ResponseEntity<Task> taskResponseEntity = restTemplate.postForEntity("/tasks", taskRequest, Task.class);
        assertThat(taskResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Task body = taskResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(task));
    }
}

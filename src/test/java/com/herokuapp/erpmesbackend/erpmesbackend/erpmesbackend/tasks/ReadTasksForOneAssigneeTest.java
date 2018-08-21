package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.tasks;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.Task;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadTasksForOneAssigneeTest extends FillBaseTemplate {

    private List<Task> tasks;

    @Before
    public void init() {
        addOneAdminRequest(true);
        tasks = addTaskRequests(true);
    }

    @Test
    public void checkIfResponseContainsAllTeams() {
        ResponseEntity<Task[]> forEntity = restTemplate.getForEntity("/employees/1/tasks", Task[].class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Task> fetchedTasks = Arrays.asList(forEntity.getBody());
        for (Task task : fetchedTasks) {
            assertTrue(tasks.stream().anyMatch(t -> t.getAssignee().getId() == task.getAssignee().getId()));
        }
    }
}

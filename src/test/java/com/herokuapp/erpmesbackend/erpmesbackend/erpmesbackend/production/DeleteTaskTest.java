package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.production;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Task;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteTaskTest extends FillBaseTemplate {

    private Task deletedTask;

    @Before
    public void init() {
        super.init();
        deletedTask = restTemplate.exchange("/tasks/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Task.class, 1).getBody();
    }

    @Test
    // @Ignore
    public void checkIfResponseDoesNotContainDeletedTask() {
        restTemplate.delete("/tasks/{id}", 1);

        List<Task> fetchedTasks = Arrays.asList(restTemplate.exchange("/tasks", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Task[].class).getBody());
        assertFalse(fetchedTasks.stream().anyMatch(task -> task.checkIfDataEquals(deletedTask)));
    }
}

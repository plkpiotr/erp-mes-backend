package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.production;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.dto.TaskDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.TaskRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
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

    private TaskDTO taskDTO;

    @Before
    public void init() {
        super.init();
        String name = taskFactory.generateName();
        List<Long> precedingTasksIds = new ArrayList<>();
        Integer estimatedTime = taskFactory.generateEstimatedTime();
        LocalDateTime deadline = taskFactory.generateDeadline();
        String details = taskFactory.generateDetails();

        Long assigneeId = 2L;
        EmployeeDTO assigneeDTO = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 2).getBody();

        for (long i = 1; i <= 4; i++)
            precedingTasksIds.add(i);

        taskRequest = new TaskRequest(name, precedingTasksIds, assigneeId, estimatedTime, deadline, null,
                null, null, details, null);
        taskDTO = new TaskDTO(name, precedingTasksIds, assigneeDTO, estimatedTime);
    }

    @Test
    public void checkIfResponseContainsAddedTask() {
        ResponseEntity<TaskDTO> taskDTOResponseEntity = restTemplate.postForEntity("/tasks",
                new HttpEntity<>(taskRequest, requestHeaders),  TaskDTO.class);
        assertThat(taskDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        TaskDTO body = taskDTOResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(taskDTO));
    }
}

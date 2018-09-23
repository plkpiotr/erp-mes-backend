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

        EmployeeDTO assigneeDTO = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody();
        Long assigneeId = assigneeDTO.getId();

        List<TaskDTO> precedingTaskDTOs = new ArrayList<>();
        List<Long> precedingTaskIds = new ArrayList<>();

        for (int i = 1; i < 4; i++) {
            TaskDTO precedingTaskDTO = restTemplate.exchange("/tasks/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), TaskDTO.class, i).getBody();
            precedingTaskIds.add(precedingTaskDTO.getId());
            precedingTaskDTOs.add(precedingTaskDTO);
        }

        String details = taskFactory.generateDetails();
        Integer estimatedTimeInMinutes = taskFactory.generateEstimatedTimeInMinutes();

        Type type = Type.ORDER;
        Long reference = 1L;

        LocalDateTime scheduledTime = taskFactory.generateScheduledTime();

        taskRequest = new TaskRequest(name, assigneeId, precedingTaskIds, details, estimatedTimeInMinutes,
                null, type, reference, scheduledTime);

        taskDTO = new TaskDTO(name, assigneeDTO, precedingTaskDTOs, details, estimatedTimeInMinutes, type, reference);
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

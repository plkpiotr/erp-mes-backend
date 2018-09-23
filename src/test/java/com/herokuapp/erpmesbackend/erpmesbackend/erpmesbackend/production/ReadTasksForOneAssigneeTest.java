package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.production;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.dto.TaskDTO;
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
public class ReadTasksForOneAssigneeTest extends FillBaseTemplate {

    private List<TaskDTO> taskDTOs;

    @Before
    public void init() {
        setupToken();
        addOneAdminRequest(true);
        taskDTOs = addTaskRequests(true);
    }

    @Test
    public void checkIfResponseContainsAllTeams() {
        ResponseEntity<TaskDTO[]> forEntity = restTemplate.exchange("/employees/1/tasks", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), TaskDTO[].class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<TaskDTO> taskDTOs = Arrays.asList(forEntity.getBody());
        for (TaskDTO taskDTO : taskDTOs) {
            assertTrue(this.taskDTOs.stream()
                    .anyMatch(t ->t.getAssigneeDTO().getId() == taskDTO.getAssigneeDTO().getId()));
        }
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.errors;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.TeamRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotAManagerTest extends FillBaseTemplate {

    private TeamRequest invalidTeamRequest;

    @Before
    public void init() {
        addOneNonAdminRequest(true);
        addNonAdminRequests(true);

        filterEmployeesByRole();
        Long[] ids = {availableEmployeeIds.get(1), availableEmployeeIds.get(2), availableEmployeeIds.get(3)};

        invalidTeamRequest = new TeamRequest(Role.ACCOUNTANT, availableEmployeeIds.get(0), Arrays.asList(ids));
    }

    @Test
    public void checkIfResponseStatus400BadRequest() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/teams",
                invalidTeamRequest, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

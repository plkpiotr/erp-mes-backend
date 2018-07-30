package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.errors;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.Holiday;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.TeamRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InvalidRequestTest extends FillBaseTemplate {

    private TeamRequest invalidManagerRequest;
    private TeamRequest invalidEmployeeRequest;

    @Before
    public void init() {
        addOneAdminRequest(true);
        addNonAdminRequests(true);
        addOneTeamRequest(true, teamRequest);
        addOneHolidayRequest(2, true);
        restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                1, String.class, 1, 2
        );

        Team team = restTemplate.getForEntity("/teams/{id}", Team.class, 1).getBody();
        List<Long> ids = new ArrayList<>();
        team.getEmployees().forEach(employee -> ids.add(employee.getId()));
        invalidManagerRequest = new TeamRequest(team.getRole(), team.getManager().getId(), ids);

        EmployeeRequest secondManagerRequest = employeeFactory.generateAdminRequest();
        Employee body = restTemplate.postForEntity("/employees", secondManagerRequest,
                Employee.class).getBody();
        invalidEmployeeRequest = new TeamRequest(team.getRole(), body.getId(), ids);
    }

    @Test
    public void checkIfResponseStatus400BadManagerRequest() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/teams",
                invalidManagerRequest, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).contains("manager");
    }

    @Test
    public void checkIfResponseStatus400BadEmployeeRequest() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/teams",
                invalidEmployeeRequest, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).contains("employee");
    }

    @Test
    public void checkIfResponseStatus400HolidayAlreadyApprovedOrDeclined() {
        ResponseEntity<String> holidayResponseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=false",
                1, String.class, 1, 2
        );
        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

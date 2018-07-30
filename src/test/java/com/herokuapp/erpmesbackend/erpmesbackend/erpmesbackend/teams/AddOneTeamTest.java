package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.teams;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddOneTeamTest extends FillBaseTemplate {

    private Team team;

    @Before
    public void init() {
        addOneAdminRequest(true);
        addNonAdminRequests(true);

        Employee manager = restTemplate.getForEntity("/employees/{id}", Employee.class, 1)
                .getBody();
        List<Employee> employees = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (int i = 2; i < 5; i++) {
            Employee employee = restTemplate.getForEntity("/employees/{id}", Employee.class, i).getBody();
            ids.add(employee.getId());
            employees.add(employee);
        }

        Role role = employeeFactory.generateRole();
        teamRequest = new TeamRequest(role, manager.getId(), ids);
        team = new Team(role, manager, employees);
    }

    @Test
    public void checkIfResponseContainsAddedTeam() {
        ResponseEntity<Team> teamResponseEntity = restTemplate.postForEntity("/teams",
                teamRequest, Team.class);

        assertThat(teamResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Team body = teamResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(team));
    }
}

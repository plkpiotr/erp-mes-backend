package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.teams;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.teams.TeamRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddOneTeamTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TeamRequest request;
    private Team team;

    @Before
    public void init() {
        EmployeeFactory employeeFactory = new EmployeeFactory();
        List<EmployeeRequest> employeeRequests = new ArrayList<>();
        EmployeeRequest managerRequest = employeeFactory.generateAdminRequest();

        restTemplate.postForEntity("/employees", managerRequest, Employee.class);

        for (int i = 0; i < 5; i++) {
            employeeRequests.add(employeeFactory.generateNonAdminRequest());
        }

        employeeRequests.forEach(request -> restTemplate.postForEntity("/employees",
                request, Employee.class));

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
        request = new TeamRequest(role, manager.getId(), ids);
        team = new Team(role, manager, employees);
    }

    @Test
    public void checkIfResponseContainsAddedTeam() {
        ResponseEntity<Team> teamResponseEntity = restTemplate.postForEntity("/teams",
                request, Team.class);

        assertThat(teamResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Team body = teamResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(team));
    }
}

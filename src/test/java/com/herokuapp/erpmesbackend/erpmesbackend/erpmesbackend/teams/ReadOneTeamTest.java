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
public class ReadOneTeamTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private List<TeamRequest> requests;
    private List<Team> teams;

    @Before
    public void init() {
        requests = new ArrayList<>();
        teams = new ArrayList<>();
        createTeam(2);
        createTeam(8);
        createTeam(14);
    }

    @Test
    public void checkIfResponseContainsTeamWithGivenId() {
        for (int i = 0; i < 3; i++) {
            ResponseEntity<Team> forEntity = restTemplate.getForEntity("/teams/{id}", Team.class, i + 1);

            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            Team team = forEntity.getBody();
            assertTrue(teams.stream().anyMatch(t -> t.checkIfDataEquals(team)));
        }
    }

    public void createTeam(int start) {
        EmployeeFactory employeeFactory = new EmployeeFactory();
        List<EmployeeRequest> employeeRequests = new ArrayList<>();
        EmployeeRequest managerRequest = employeeFactory.generateAdminRequest();

        restTemplate.postForEntity("/employees", managerRequest, Employee.class);

        for (int i = 0; i < 5; i++) {
            employeeRequests.add(employeeFactory.generateNonAdminRequest());
        }

        employeeRequests.forEach(request -> restTemplate.postForEntity("/employees",
                request, Employee.class));

        Employee manager = restTemplate.getForEntity("/employees/{id}", Employee.class, start - 1)
                .getBody();
        List<Employee> employees = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (int i = start; i < start + 3; i++) {
            Employee employee = restTemplate.getForEntity("/employees/{id}", Employee.class, i).getBody();
            ids.add(employee.getId());
            employees.add(employee);
        }

        Role role = employeeFactory.generateRole();
        TeamRequest request = new TeamRequest(role, manager.getId(), ids);
        restTemplate.postForEntity("/teams", request, Team.class);
        requests.add(request);
        teams.add(new Team(role, manager, employees));
    }
}

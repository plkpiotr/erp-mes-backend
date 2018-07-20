package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.errors;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Role;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InvalidRequestTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TeamRequest invalidManagerRequest;
    private TeamRequest invalidEmployeeRequest;

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
        TeamRequest validRequest = new TeamRequest(role, manager.getId(), ids);

        restTemplate.postForEntity("/teams", validRequest, String.class);

        invalidManagerRequest = new TeamRequest(role, manager.getId(), ids);

        EmployeeRequest secondManagerRequest = employeeFactory.generateAdminRequest();
        Employee body = restTemplate.postForEntity("/employees", secondManagerRequest,
                Employee.class).getBody();
        invalidEmployeeRequest = new TeamRequest(role, body.getId(), ids);
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
}

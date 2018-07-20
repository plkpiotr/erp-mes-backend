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
public class NotAManagerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TeamRequest request;
    private EmployeeFactory employeeFactory;

    @Before
    public void init() {
        employeeFactory = new EmployeeFactory();
        EmployeeRequest invalidManagerRequest = employeeFactory.generateNonAdminRequest();
        List<Employee> employees = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Employee employee = employeeFactory.generateNonAdmin();
            employees.add(employee);
            ids.add(employee.getId());
        }

        Employee body = restTemplate.postForEntity("/employees", invalidManagerRequest,
                Employee.class).getBody();

        request = new TeamRequest(Role.ACCOUNTANT, body.getId(), ids);
    }

    @Test
    public void checkIfResponseStatus400BadRequest() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/teams",
                request, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

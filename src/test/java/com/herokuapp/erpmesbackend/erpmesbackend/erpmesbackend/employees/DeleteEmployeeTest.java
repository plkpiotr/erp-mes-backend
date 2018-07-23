package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeRequest;
import org.junit.After;
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
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteEmployeeTest extends FillBaseTemplate {

    private Employee deletedEmployee;

    @Before
    public void init() {
        addEmployeeRequests(true);
        deletedEmployee = restTemplate.getForEntity("/employees/{id}", Employee.class, 1)
                .getBody();
    }

    @Test
    public void checkIfResponseDoesNotContainDeletedEmployee() {
        restTemplate.delete("/employees/{id}", 1);

        List<Employee> employees = Arrays.asList(restTemplate.getForEntity("/employees",
                Employee[].class).getBody());
        assertFalse(employees.stream().anyMatch(employee -> employee.checkIfDataEquals(deletedEmployee)));
    }

    @After
    public void clean() {
        for (int i = 0; i < 10; i++) {
            restTemplate.delete("/employees/{id}", i + 1);
        }
    }
}

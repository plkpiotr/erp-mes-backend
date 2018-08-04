package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddOneEmployeeTest extends FillBaseTemplate {

    @Before
    public void init() {
        addOneEmployeeRequest(false);
    }

    @Test
    public void checkIfResponseContainsAddedEmployee() {
        ResponseEntity<Employee> employeeResponseEntity = restTemplate
                .postForEntity("/employees", employeeRequest, Employee.class);

        assertThat(employeeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Employee body = employeeResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(employeeRequest.extractUser()));
    }

    @After
    public void clean() {
        restTemplate.delete("/employees/{id}", 1);
    }
}

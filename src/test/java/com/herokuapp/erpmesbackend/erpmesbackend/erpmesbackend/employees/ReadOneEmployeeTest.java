package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import org.junit.After;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadOneEmployeeTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addNonAdminRequests(true);
    }

    @Test
    public void checkIfResponseContainsCalledEmployee() {
        for (int i = 0; i < employeeRequests.size(); i++) {
            ResponseEntity<Employee> forEntity = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Employee.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertTrue(employeeRequests.stream().anyMatch(request -> request.extractUser()
                    .checkIfDataEquals(forEntity.getBody())));
        }
    }
}

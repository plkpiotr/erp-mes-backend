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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadAllAdminsTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addEmployeeRequests(true);
    }

    @Test
    public void checkIfResponseContainsAllManagers() {
        long adminCount = employeeRequests.stream()
                .filter(request -> request.getRole().name().contains("ADMIN"))
                .map(request -> request.getRole())
                .distinct()
                .count();

        ResponseEntity<Employee[]> forEntity = restTemplate.exchange("/employees?privilege=admin",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders), Employee[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Employee> employees = Arrays.asList(forEntity.getBody());
        assertThat(employees.size()).isEqualTo(adminCount+1);
    }

    @After
    public void clean() {
        for(int i = 0; i < 10; i++) {
            restTemplate.delete("/employees/{id}", i+1);
        }
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadAllEmployeesTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addEmployeeRequests(true);
    }

    @Test
    public void checkIfResponseContainsAllEmployees() {
        ResponseEntity<EmployeeDTO[]> forEntity = restTemplate.exchange("/employees", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<EmployeeDTO> employees = Arrays.asList(forEntity.getBody());
        assertThat(employees.size()).isGreaterThanOrEqualTo(employeeRequests.stream()
                .map(employee -> employee.getRole())
                .distinct()
                .collect(Collectors.toList())
                .size());
    }

    @After
    public void clean() {
        for (int i = 0; i < 10; i++) {
            restTemplate.delete("/employees/{id}", i + 1);
        }
    }
}
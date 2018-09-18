package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.errors;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotAManagerTest extends FillBaseTemplate {

    private EmployeeDTO employee;

    @Before
    public void init() {
        setupToken();
        addOneNonAdminRequest(true);
        employee = Arrays.asList(restTemplate.exchange("/employees", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class).getBody())
                .stream()
                .filter(employee -> !employee.getRole().name().contains("ADMIN"))
                .findFirst()
                .get();
    }

    @Test
    public void checkIfResponse400NotAManager() {
        ResponseEntity<String> forEntity = restTemplate.exchange("/employees/{id}/subordinates",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders), String.class,
                employee.getId());
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

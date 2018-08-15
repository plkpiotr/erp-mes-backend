package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.errors;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotFoundTest extends FillBaseTemplate {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void checkIfResponseStatus404EmployeeNotFound() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity("/employees/{id}",
                String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void checkIfResponseStatus404TeamNotFound() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity("/teams/{id}",
                String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void checkIfResponseStatus404HolidayNotFound() {
        addOneAdminRequest(false);
        addOneNonAdminRequest(false);
        adminRequest.setRole(Role.ADMIN_ACCOUNTANT);
        nonAdminRequest.setRole(Role.ACCOUNTANT);
        restTemplate.postForEntity("/employees", adminRequest, String.class);
        restTemplate.postForEntity("/employees", nonAdminRequest, String.class);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                1, String.class, 1, 2);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void checkIfResponseStatus404ReportNotFound() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity("/reports/{id}", String.class, 10);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void checkIfResponseStatus404ItemNotFound() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity("/items/{id}", String.class, 255);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void checkIfResponseStatus404DeliveryNotFound() {
        ResponseEntity<String> forEntity = restTemplate.getForEntity("/deliveries/{id}", String.class, 255);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

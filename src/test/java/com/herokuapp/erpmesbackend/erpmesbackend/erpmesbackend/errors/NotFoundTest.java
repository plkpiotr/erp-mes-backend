package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.errors;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
        setupToken();
        ResponseEntity<String> forEntity = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void checkIfResponseStatus404TeamNotFound() {
        setupToken();
        ResponseEntity<String> forEntity = restTemplate.exchange("/teams/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void checkIfResponseStatus404HolidayNotFound() {
        setupToken();
        addOneAdminRequest(false);
        addOneNonAdminRequest(false);
        adminRequest.setRole(Role.ADMIN_ACCOUNTANT);
        nonAdminRequest.setRole(Role.ACCOUNTANT);
        restTemplate.postForEntity("/employees", new HttpEntity<>(adminRequest, requestHeaders), String.class);
        restTemplate.postForEntity("/employees", new HttpEntity<>(nonAdminRequest, requestHeaders), String.class);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                new HttpEntity<>(1, requestHeaders), String.class, 2, 3);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void checkIfResponseStatus404ReportNotFound() {
        setupToken();
        ResponseEntity<String> forEntity = restTemplate.exchange("/reports/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 10);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void checkIfResponseStatus404ItemNotFound() {
        setupToken();
        ResponseEntity<String> forEntity = restTemplate.exchange("/items/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 255);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void checkIfResponseStatus404DeliveryNotFound() {
        setupToken();
        ResponseEntity<String> forEntity = restTemplate.exchange("/deliveries/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 255);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

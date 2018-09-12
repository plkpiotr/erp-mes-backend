package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.errors;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.Holiday;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.HolidayType;
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

import java.util.Arrays;

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
        addAdminRequests(true);
        EmployeeDTO employee = Arrays.asList(restTemplate.exchange("/employees", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class).getBody())
                .stream()
                .filter(e -> e.getRole().name().contains("ADMIN_"))
                .findFirst()
                .get();
        addOneHolidayRequest(employee.getId(), false);
        holidayRequest.setHolidayType(HolidayType.VACATION);
        Holiday holiday = restTemplate.postForEntity(
                "/employees/{id}/holidays", new HttpEntity<>(holidayRequest, requestHeaders),
                Holiday.class, employee.getId()).getBody();

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                new HttpEntity<>(holiday.getId()+1, requestHeaders), String.class, 1,
                employee.getId());
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

    @Test
    public void checkIfResponseStatus404EmailNotFound() {
        setupToken();
        ResponseEntity<String> forEntity = restTemplate.exchange("/emails/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 255);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

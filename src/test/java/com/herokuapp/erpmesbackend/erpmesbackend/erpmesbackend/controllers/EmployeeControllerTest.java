package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.ContractRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.EmployeeRequest;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupEmployees();
    }

    @Test
    public void readAllEmployeesTest() {
        ResponseEntity<EmployeeDTO[]> forEntity = restTemplate.exchange("/employees", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<EmployeeDTO> employeeDTOS = Arrays.asList(forEntity.getBody());
        employeeRequests.forEach(request -> employeeDTOS.stream()
                .anyMatch(employeeDTO -> checkIfDtoAndRequestMatch(employeeDTO, request)));
    }

    @Test
    public void readAllAdminsTest() {
        ResponseEntity<EmployeeDTO[]> forEntity = restTemplate.exchange("/employees?privilege=admin",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<EmployeeDTO> employees = Arrays.asList(forEntity.getBody());
        assertThat(employees.size()).isEqualTo(4);
    }

    @Test
    public void readAllOrdinaryUsersTest() {
        ResponseEntity<Employee[]> forEntity = restTemplate.exchange("/employees?privilege=user", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Employee> employees = Arrays.asList(forEntity.getBody());
        assertThat(employees.size()).isGreaterThan(0);
        assertThat(employees.stream().filter(employee -> employee.getRole().name().contains("ADMIN")).count())
                .isZero();
    }

    @Test
    public void readOneEmployeeTest() {
        for (int i = 0; i <= employeeRequests.size(); i++) {
            ResponseEntity<EmployeeDTO> forEntity = restTemplate.exchange("/employees/{id}",
                    HttpMethod.GET, new HttpEntity<>(null, requestHeaders),
                    EmployeeDTO.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(forEntity.getBody()).isNotNull();
        }
    }

    @Test
    public void addOneEmployeeTest() {
        EmployeeRequest oneEmployeeRequest = getOneEmployeeRequest();
        ResponseEntity<EmployeeDTO> employeeResponseEntity = restTemplate
                .postForEntity("/employees", new HttpEntity<>(oneEmployeeRequest, requestHeaders),
                        EmployeeDTO.class);
        assertThat(employeeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertTrue(checkIfDtoAndRequestMatch(employeeResponseEntity.getBody(), oneEmployeeRequest));
    }

    @Test
    public void deleteEmployeeTest() {
        EmployeeRequest oneEmployeeRequest = getOneEmployeeRequest();
        EmployeeDTO body = restTemplate.postForEntity("/employees", new HttpEntity<>(oneEmployeeRequest, requestHeaders),
                EmployeeDTO.class).getBody();

        restTemplate.delete("/employees/{id}", body.getId());
        List<EmployeeDTO> employeeDTOS = Arrays.asList(restTemplate.exchange("/employees", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class).getBody());
        assertThat(employeeDTOS.stream().noneMatch(employeeDTO -> employeeDTO.getId() == body.getId()));
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> forEntity = restTemplate.exchange("/employees/{id}",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders),
                String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReturn400NotAManager() {
        Employee[] body = restTemplate.exchange("/employees?privilege=user", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee[].class).getBody();
        assertThat(body).isNotNull();
        assertThat(body.length).isGreaterThan(0);

        ResponseEntity<String> forEntity = restTemplate.exchange("/employees/{id}/subordinates",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders), String.class,
                body[0].getId());
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturn400BadRequestForDuplicateAdmin() {
        ContractRequest contractRequest = new ContractRequest("123", 26, 50000);
        EmployeeRequest request = new EmployeeRequest("Szef", "Ceo", "szef.ceo@company.com",
                Role.ADMIN_ACCOUNTANT, contractRequest);
        ResponseEntity<String> employeeResponseEntity = restTemplate
                .postForEntity("/employees", new HttpEntity<>(request, requestHeaders),
                        String.class);
        assertThat(employeeResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private boolean checkIfDtoAndRequestMatch(EmployeeDTO employeeDTO, EmployeeRequest request) {
        return request.getFirstName().equals(employeeDTO.getFirstName()) &&
                request.getLastName().equals(employeeDTO.getLastName()) &&
                request.getEmail().equals(employeeDTO.getEmail()) &&
                request.getRole().equals(employeeDTO.getRole());
    }
}
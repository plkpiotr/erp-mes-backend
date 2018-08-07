package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.errors;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.Holiday;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InvalidRequestTest extends FillBaseTemplate {

    @Before
    public void init() {
        addOneAdminRequest(false);
        addOneNonAdminRequest(false);
        adminRequest.setRole(Role.ADMIN_ACCOUNTANT);
        nonAdminRequest.setRole(Role.ACCOUNTANT);
        restTemplate.postForEntity("/employees", adminRequest, String.class);
        restTemplate.postForEntity("/employees", nonAdminRequest, String.class);
        addOneHolidayRequest(2, true);
        restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                1, String.class, 1, 2
        );
    }

    @Test
    public void checkIfResponse400InvalidRequest() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/employees",
                adminRequest, String.class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void checkIfResponse400InvalidApprovalRequest() {
        ResponseEntity<String> holidayResponseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                1, String.class, 2, 3
        );
        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void checkIfResponse400VacationAlreadyUsed() {
        addOneNonAdminRequest(false);
        nonAdminRequest.getContractRequest().setDaysOffPerYear(20);
        ResponseEntity<Employee> employeeResponseEntity = restTemplate.postForEntity("/employees",
                nonAdminRequest, Employee.class);
        long id = employeeResponseEntity.getBody().getId();
        addOneHolidayRequest(id, false);
        holidayRequest.setDuration(20);
        restTemplate.postForEntity("/employees/{id}/holidays", holidayRequest, Holiday.class, id);
        addOneHolidayRequest(id, false);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/employees/{id}/holidays",
                holidayRequest, String.class, id);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).contains("already used all");
    }

    @Test
    public void checkIfResponse400VacationTooLong() {
        addOneNonAdminRequest(false);
        nonAdminRequest.getContractRequest().setDaysOffPerYear(20);
        ResponseEntity<Employee> employeeResponseEntity = restTemplate.postForEntity("/employees",
                nonAdminRequest, Employee.class);
        long id = employeeResponseEntity.getBody().getId();
        addOneHolidayRequest(id, false);
        holidayRequest.setDuration(15);
        restTemplate.postForEntity("/employees/{id}/holidays", holidayRequest, Holiday.class, id);
        addOneHolidayRequest(id, false);
        holidayRequest.setDuration(7);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/employees/{id}/holidays",
                holidayRequest, String.class, id);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseEntity.getBody()).contains("have enough");
    }
}

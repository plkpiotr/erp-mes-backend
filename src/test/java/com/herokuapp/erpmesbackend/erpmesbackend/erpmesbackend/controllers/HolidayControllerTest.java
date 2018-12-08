package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.ApprovalState;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Holiday;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.HolidayType;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.HolidayRequest;
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
public class HolidayControllerTest extends TestConfig {

    private long adminAccountantId;

    @Before
    public void init() {
        setup();
        setupToken();
        setupEmployees();
        setupHolidays();

        List<EmployeeDTO> employeeDTOS = Arrays.asList(restTemplate.exchange("/employees",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class).getBody());
        employeeDTOS.stream()
                .filter(employeeDTO -> employeeDTO.getRole().equals(Role.ADMIN_ACCOUNTANT))
                .findFirst()
                .ifPresent(employeeDTO -> adminAccountantId = employeeDTO.getId());
    }

    @Test
    public void readAllHolidaysTest() {
        ResponseEntity<Holiday[]> forEntity = restTemplate.exchange("/employees/{id}/holidays", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Holiday[].class, adminAccountantId);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Holiday> holidays = Arrays.asList(forEntity.getBody());
        holidayRequests.forEach(request -> holidays.stream()
                .anyMatch(holiday -> checkIfHolidayAndRequestMatch(holiday, request)));
    }

    @Test
    public void addNewHolidayPendingApprovalTest() {
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity("/employees/{id}/holidays",
                new HttpEntity<>(getOneHolidayRequest(HolidayType.VACATION), requestHeaders),
                Holiday.class, adminAccountantId);
        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(holidayResponseEntity.getBody().getApprovalState()).isEqualTo(ApprovalState.PENDING);
    }

    @Test
    public void addNewHolidayNotPendingApprovalTest() {
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity("/employees/{id}/holidays",
                new HttpEntity<>(getOneHolidayRequest(HolidayType.SICK_LEAVE), requestHeaders),
                Holiday.class, adminAccountantId);
        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(holidayResponseEntity.getBody().getApprovalState()).isEqualTo(ApprovalState.APPROVED);
    }

    @Test
    public void managerReadHolidayRequestsTest() {
        ResponseEntity<Holiday[]> exchange = restTemplate.exchange(
                "/employees/{managerId}/subordinates/holiday-requests", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Holiday[].class, ADMIN_ID);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getBody().length).isNotZero();
    }

    @Test
    public void approveHolidayTest() {
        Holiday body = restTemplate.postForEntity("/employees/{id}/holidays",
                new HttpEntity<>(getOneHolidayRequest(HolidayType.VACATION), requestHeaders),
                Holiday.class, adminAccountantId).getBody();
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                new HttpEntity<>(body.getId(), requestHeaders), Holiday.class,
                ADMIN_ID, adminAccountantId);

        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(holidayResponseEntity.getBody().getApprovalState()).isEqualTo(ApprovalState.APPROVED);
    }

    @Test
    public void declineHolidayTest() {
        Holiday body = restTemplate.postForEntity("/employees/{id}/holidays",
                new HttpEntity<>(getOneHolidayRequest(HolidayType.VACATION), requestHeaders),
                Holiday.class, adminAccountantId).getBody();
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=false",
                new HttpEntity<>(body.getId(), requestHeaders), Holiday.class, ADMIN_ID, adminAccountantId);

        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(holidayResponseEntity.getBody().getApprovalState()).isEqualTo(ApprovalState.DECLINED);
    }

    private boolean checkIfHolidayAndRequestMatch(Holiday holiday,
                                                  HolidayRequest holidayRequest) {
        return holiday.getStartDate().equals(holidayRequest.getStartDate()) &&
                holiday.getDuration() == holidayRequest.getDuration() &&
                holiday.getHolidayType().equals(holidayRequest.getHolidayType());
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailEntity;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.security.Credentials;
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
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HolidayControllerTest extends TestConfig {

    private EmployeeDTO employeeDTO;

    @Before
    public void init() {
        setup();
        setupToken();
        setupEmployees();
        setupHolidays();

        List<EmployeeDTO> employeeDTOS = Arrays.asList(restTemplate.exchange("/employees",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class).getBody());
        employeeDTOS.stream()
                .filter(dto -> dto.getRole().equals(Role.ADMIN_ACCOUNTANT))
                .findFirst()
                .ifPresent(dto -> employeeDTO = dto);
    }

    @Test
    public void readAllHolidaysTest() {
        ResponseEntity<Holiday[]> forEntity = restTemplate.exchange("/employees/{id}/holidays", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Holiday[].class, employeeDTO.getId());
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Holiday> holidays = Arrays.asList(forEntity.getBody());
        holidayRequests.forEach(request -> holidays.stream()
                .anyMatch(holiday -> checkIfHolidayAndRequestMatch(holiday, request)));
    }

    @Test
    public void addNewHolidayPendingApprovalTest() {
        HttpHeaders newHeaders = loginNewUser();
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity("/employees/{id}/holidays",
                new HttpEntity<>(getOneHolidayRequest(HolidayType.VACATION), newHeaders),
                Holiday.class, employeeDTO.getId());
        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(holidayResponseEntity.getBody().getApprovalState()).isEqualTo(ApprovalState.PENDING);
    }

    @Test
    public void addNewHolidayNotPendingApprovalTest() {
        HttpHeaders newHeaders = loginNewUser();
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity("/employees/{id}/holidays",
                new HttpEntity<>(getOneHolidayRequest(HolidayType.SICK_LEAVE), newHeaders),
                Holiday.class, employeeDTO.getId());
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
        HttpHeaders newHeaders = loginNewUser();
        Holiday body = restTemplate.postForEntity("/employees/{id}/holidays",
                new HttpEntity<>(getOneHolidayRequest(HolidayType.VACATION), newHeaders),
                Holiday.class, employeeDTO.getId()).getBody();
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                new HttpEntity<>(body.getId(), requestHeaders), Holiday.class,
                ADMIN_ID, employeeDTO.getId());

        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(holidayResponseEntity.getBody().getApprovalState()).isEqualTo(ApprovalState.APPROVED);
    }

    @Test
    public void declineHolidayTest() {
        HttpHeaders newHeaders = loginNewUser();
        Holiday body = restTemplate.postForEntity("/employees/{id}/holidays",
                new HttpEntity<>(getOneHolidayRequest(HolidayType.VACATION), newHeaders),
                Holiday.class, employeeDTO.getId()).getBody();
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=false",
                new HttpEntity<>(body.getId(), requestHeaders), Holiday.class, ADMIN_ID, employeeDTO.getId());

        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(holidayResponseEntity.getBody().getApprovalState()).isEqualTo(ApprovalState.DECLINED);
    }

    private boolean checkIfHolidayAndRequestMatch(Holiday holiday,
                                                  HolidayRequest holidayRequest) {
        return holiday.getStartDate().equals(holidayRequest.getStartDate()) &&
                holiday.getDuration() == holidayRequest.getDuration() &&
                holiday.getHolidayType().equals(holidayRequest.getHolidayType());
    }

    private HttpHeaders loginNewUser() {
        EmailEntity emailEntity = readOutbox().stream()
                .filter(mail -> mail.getEmail().equals(employeeDTO.getEmail()))
                .findFirst().get();
        String password = extractPassword(emailEntity);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/generate-token",
                new Credentials(employeeDTO.getEmail(),
                        password), String.class);
        String token = responseEntity.getBody()
                .replace("{\"token\":\"", "")
                .replace("\"}", "");
        HttpHeaders newHeaders = new HttpHeaders();
        newHeaders.setContentType(MediaType.APPLICATION_JSON);
        newHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        newHeaders.add("Authorization", "Bearer " + token);
        return newHeaders;
    }

    private String extractPassword(EmailEntity emailEntity) {
        return emailEntity.getContent().get(0)
                .replace("Your automatically generated password is: ", "")
                .replace(". You will be prompt to change it after your first login attempt.", "");
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.holidays;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.ApprovalState;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Holiday;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.HolidayType;
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
public class ManageHolidayRequestsTest extends FillBaseTemplate {

    private EmployeeDTO employee;
    private Holiday holiday;

    @Before
    public void init() {
        setupToken();
        addAdminRequests(true);
        employee = Arrays.asList(restTemplate.exchange("/employees", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class).getBody())
                .stream()
                .filter(e -> e.getRole().name().contains("ADMIN_"))
                .findFirst()
                .get();

        addOneHolidayRequest(employee.getId(), false);
        holidayRequest.setHolidayType(HolidayType.VACATION);
        holidayRequest.setDuration(1);
        holiday = restTemplate.postForEntity(
                "/employees/{id}/holidays", new HttpEntity<>(holidayRequest, requestHeaders),
                Holiday.class, employee.getId()).getBody();
    }

    @Test
    public void approveHoliday() {
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                new HttpEntity<>(holiday.getId(), requestHeaders), Holiday.class,
                1, employee.getId());

        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(holidayResponseEntity.getBody().getApprovalState()).isEqualTo(ApprovalState.APPROVED);
    }

    @Test
    public void declineHoliday() {
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=false",
                new HttpEntity<>(2, requestHeaders), Holiday.class, 1, 2
        );

        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(holidayResponseEntity.getBody().getApprovalState()).isEqualTo(ApprovalState.DECLINED);
    }
}

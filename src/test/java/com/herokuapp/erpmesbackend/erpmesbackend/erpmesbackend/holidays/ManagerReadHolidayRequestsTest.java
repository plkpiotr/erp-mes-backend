package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.holidays;

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

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManagerReadHolidayRequestsTest extends FillBaseTemplate {

    @Before
    public void init() {
        addOneAdminRequest(false);
        addOneNonAdminRequest(false);
        adminRequest.setRole(Role.ADMIN_ACCOUNTANT);
        nonAdminRequest.setRole(Role.ACCOUNTANT);
        restTemplate.postForEntity("/employees", adminRequest, String.class);
        restTemplate.postForEntity("/employees", nonAdminRequest, String.class);
        addOneHolidayRequest(2, true);
    }

    @Test
    public void checkIfResponseContainsHoliday() {
        ResponseEntity<Holiday[]> holidayResponseEntity = restTemplate.getForEntity(
                "/employees/{managerId}/subordinates/holiday-requests",
                Holiday[].class, 1
        );

        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Holiday holiday = Arrays.asList(holidayResponseEntity.getBody()).get(0);
        assertTrue(holiday.getStartDate().equals(holidayRequest.getStartDate()) &&
                holiday.getDuration() == holidayRequest.getDuration() &&
                holiday.getHolidayType().equals(holidayRequest.getHolidayType()));
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.staff;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ManagerReadHolidayRequestsTest extends FillBaseTemplate {

    private EmployeeDTO employee;
    @Before
    public void init() {
        super.init();
        employee = Arrays.asList(restTemplate.exchange("/employees", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class).getBody())
                .stream()
                .filter(e -> e.getRole().name().contains("ADMIN_"))
                .findFirst()
                .get();
        addOneHolidayRequest(employee.getId(), false);
        holidayRequest.setHolidayType(HolidayType.VACATION);
        restTemplate.postForEntity("/employees/{id}/holidays",
                new HttpEntity<>(holidayRequest, requestHeaders), Holiday.class, employee.getId());
    }

    @Test
    public void checkIfResponseContainsHoliday() {
        ResponseEntity<Holiday[]> holidayResponseEntity = restTemplate.exchange(
                "/employees/{managerId}/subordinates/holiday-requests", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Holiday[].class, 1
        );

        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Holiday[] body = holidayResponseEntity.getBody();
        Holiday holiday = Arrays.asList(body).get(body.length-1);
        assertTrue(holiday.getStartDate().equals(holidayRequest.getStartDate()) &&
                holiday.getDuration() == holidayRequest.getDuration() &&
                holiday.getHolidayType().equals(holidayRequest.getHolidayType()));
    }
}

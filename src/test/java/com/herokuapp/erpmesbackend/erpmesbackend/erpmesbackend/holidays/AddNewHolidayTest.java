package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.holidays;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.Holiday;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddNewHolidayTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addOneAdminRequest(true);
        addNonAdminRequests(true);
        addOneHolidayRequest(2, false);
    }

    @Test
    public void checkIfResponseContainsAddedHoliday() {
        ResponseEntity<Holiday> holidayResponseEntity = restTemplate.postForEntity(
                "/employees/{id}/holidays", new HttpEntity<>(holidayRequest, requestHeaders), Holiday.class, 2
        );

        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Holiday holiday = holidayResponseEntity.getBody();
        assertTrue(holiday.getStartDate().equals(holidayRequest.getStartDate()));
        assertTrue(holiday.getDuration() == holidayRequest.getDuration());
        assertTrue(holiday.getHolidayType().equals(holidayRequest.getHolidayType()));
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.holidays;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Holiday;
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
public class ReadOneHolidayTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addOneAdminRequest(true);
        addNonAdminRequests(true);
        addOneHolidayRequest(2, true);
    }

    @Test
    public void checkIfResponseContainsHoliday() {
        ResponseEntity<Holiday[]> holidayResponseEntity = restTemplate.exchange(
                "/employees/{id}/holidays", HttpMethod.GET, new HttpEntity<>(null, requestHeaders),
                Holiday[].class, 2
        );

        assertThat(holidayResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        Holiday holiday = Arrays.asList(holidayResponseEntity.getBody()).get(holidayResponseEntity
                .getBody().length-1);
        assertTrue(holiday.getStartDate().equals(holidayRequest.getStartDate()));
        assertTrue(holiday.getDuration() == holidayRequest.getDuration());
        assertTrue(holiday.getHolidayType().equals(holidayRequest.getHolidayType()));
    }
}

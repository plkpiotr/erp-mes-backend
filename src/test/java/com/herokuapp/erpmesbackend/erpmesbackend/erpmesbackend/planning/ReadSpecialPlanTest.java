package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.planning;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.SpecialPlan;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadSpecialPlanTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addSpecialPlanRequest(true);
    }

    @Test
    public void checkIfReponseContainsRequestedPlan() {
        String date = specialPlanRequest.getDay().format(DateTimeFormatter.ISO_DATE);
        ResponseEntity<SpecialPlan> exchange = restTemplate.exchange("/special-plan?day=" + date, HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), SpecialPlan.class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertTrue(exchange.getBody().checkIfDataEquals(specialPlanRequest.extractSpecialPlan()));
    }
}

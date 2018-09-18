package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.planning;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.DailyPlan;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.DailyPlanRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateDailyPlanTest extends FillBaseTemplate {

    @Test
    public void checkIfResponseContainsUpdatedPlan() {
        setupToken();
        DailyPlanRequest request = new DailyPlanRequest(10, 30, 10, 15);
        ResponseEntity<DailyPlan> exchange = restTemplate.exchange("/daily-plan", HttpMethod.PUT,
                new HttpEntity<>(request, requestHeaders), DailyPlan.class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);

        DailyPlan body = exchange.getBody();
        assertTrue(body.getId() == 1);
        assertTrue(body.getComplaintsResolvedPerDay() != 5);
        assertTrue(body.getEmployeesPerDay() != 5);
        assertTrue(body.getOrdersPerDay() != 15);
        assertTrue(body.getReturnsPerDay() != 5);
    }
}

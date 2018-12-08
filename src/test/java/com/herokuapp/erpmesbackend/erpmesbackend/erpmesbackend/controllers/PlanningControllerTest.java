package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.DailyPlan;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.SpecialPlan;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.DailyPlanRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.SpecialPlanRequest;
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
public class PlanningControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupSpecialPlans();
    }

    @Test
    public void addSpecialPlanTest() {
        ResponseEntity<SpecialPlan> specialPlanResponseEntity = restTemplate.postForEntity("/special-plan",
                new HttpEntity<>(getOneSpecialPlanRequest(), requestHeaders), SpecialPlan.class);
        assertThat(specialPlanResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void readAllSpecialPlans() {
        ResponseEntity<SpecialPlan[]> forEntity = restTemplate.exchange("/special-plans", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), SpecialPlan[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<SpecialPlan> specialPlans = Arrays.asList(forEntity.getBody());
        specialPlanRequests.forEach(request -> specialPlans.stream()
                .anyMatch(specialPlan -> checkIfSpecialPlanAndRequestMatch(specialPlan, request))
        );
    }

    @Test
    public void readOneSpecialPlanTest() {
        for (int i = 0; i < specialPlanRequests.size(); i++) {
            ResponseEntity<SpecialPlan> forEntity = restTemplate.exchange("/special-plans/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), SpecialPlan.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(forEntity.getBody()).isNotNull();
        }
    }

    @Test
    public void readDailyPlanTest() {
        ResponseEntity<DailyPlan> exchange = restTemplate.exchange("/daily-plan", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), DailyPlan.class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getBody()).isNotNull();
    }

    @Test
    public void updateDailyPlanTest() {
        DailyPlan body = restTemplate.exchange("/daily-plan", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), DailyPlan.class).getBody();
        DailyPlanRequest request = new DailyPlanRequest(10, 30, 10, 15);
        ResponseEntity<DailyPlan> exchange = restTemplate.exchange("/daily-plan", HttpMethod.PUT,
                new HttpEntity<>(request, requestHeaders), DailyPlan.class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getBody()).isNotEqualTo(body);
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> forEntity = restTemplate.exchange("/special-plans/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private boolean checkIfSpecialPlanAndRequestMatch(SpecialPlan specialPlan, SpecialPlanRequest request) {
        return request.getDescription().equals(specialPlan.getDescription()) &&
                request.getDay().equals(specialPlan.getDay()) &&
                request.getEmployeesPerDay() == specialPlan.getEmployeesPerDay() &&
                request.getOrdersPerDay() == specialPlan.getOrdersPerDay() &&
                request.getReturnsPerDay() == specialPlan.getReturnsPerDay() &&
                request.getComplaintsResolvedPerDay() == specialPlan.getComplaintsResolvedPerDay();
    }
}

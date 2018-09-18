package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.planning;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.SpecialPlan;
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
public class AddSpecialPlanTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addSpecialPlanRequest(false);
    }

    @Test
    public void checkIfResponseContainsAddedPlan() {
        ResponseEntity<SpecialPlan> specialPlanResponseEntity = restTemplate.postForEntity("/special-plan",
                new HttpEntity<>(specialPlanRequest, requestHeaders), SpecialPlan.class);
        assertThat(specialPlanResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertTrue(specialPlanResponseEntity.getBody().checkIfDataEquals(specialPlanRequest.extractSpecialPlan()));
    }
}

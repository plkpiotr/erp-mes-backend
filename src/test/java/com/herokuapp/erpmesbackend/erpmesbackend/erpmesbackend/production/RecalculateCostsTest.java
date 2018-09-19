package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.production;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.CurrentReport;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.EstimatedCosts;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.EstimatedCostsRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecalculateCostsTest extends FillBaseTemplate {

    @Test
    public void checkIfResponseContainsUpdatedCosts() {
        setupToken();
        EstimatedCostsRequest estimatedCostsRequest = new EstimatedCostsRequest(100000.00,
                10000.00, 10000.00, 10000.00,
                10000.00, 10000.00, 10000.00, 10000.00);
        restTemplate.exchange("/current-report", HttpMethod.PUT, new HttpEntity<>(estimatedCostsRequest,
                requestHeaders), String.class);

        ResponseEntity<CurrentReport> forEntity = restTemplate.exchange("/current-report", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), CurrentReport.class);
        EstimatedCosts estimatedCosts = forEntity.getBody().getEstimatedCosts();
        assertThat(estimatedCosts.getEstimatedIncome()).isEqualTo(100000.00);
        assertThat(estimatedCosts.getEstimatedShippingCosts()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getEstimatedBills()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getRent()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getSalaries()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getStockCosts()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getSocialFund()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getUnexpected()).isEqualTo(10000.00);
    }
}

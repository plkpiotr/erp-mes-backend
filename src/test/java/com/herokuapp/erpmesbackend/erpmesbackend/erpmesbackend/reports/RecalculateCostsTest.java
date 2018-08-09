package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.reports;

import com.herokuapp.erpmesbackend.erpmesbackend.finance.CurrentReport;
import com.herokuapp.erpmesbackend.erpmesbackend.finance.EstimatedCosts;
import com.herokuapp.erpmesbackend.erpmesbackend.finance.EstimatedCostsRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RecalculateCostsTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void checkIfResponseContainsUpdatedCosts() {
        EstimatedCostsRequest estimatedCostsRequest = new EstimatedCostsRequest(100000.00,
                10000.00, 10000.00, 10000.00,
                10000.00, 10000.00, 10000.00, 10000.00);
        restTemplate.put("/current-report", estimatedCostsRequest);

        ResponseEntity<CurrentReport> forEntity = restTemplate.getForEntity("/current-report",
                CurrentReport.class);
        EstimatedCosts estimatedCosts = forEntity.getBody().getEstimatedCosts();
        assertThat(estimatedCosts.getEstimatedIncome()).isEqualTo(100000.00);
        assertThat(estimatedCosts.getEstimatedShippingCosts()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getEstimatedBills()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getRent()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getSalaries()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getStockCosts()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getSocialFund()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getUnexpected()).isEqualTo(10000.00);
        assertThat(estimatedCosts.getTaxes()).isEqualTo(22000.00);
    }
}

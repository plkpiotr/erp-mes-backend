package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.production;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.CurrentReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddNewIncomeTest extends FillBaseTemplate {

    @Test
    public void checkIfResponseIncomeIncreased() {
        setupToken();
        CurrentReport report = restTemplate.exchange("/current-report", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), CurrentReport.class).getBody();
        assertThat(!report.getIncome().contains(2000.00));
        ResponseEntity<CurrentReport> currentReportResponseEntity = restTemplate.postForEntity(
                "/current-report/income", new HttpEntity<>(2000.00, requestHeaders), CurrentReport.class);
        assertThat(currentReportResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(currentReportResponseEntity.getBody().getIncome().contains(2000.00));
    }
}

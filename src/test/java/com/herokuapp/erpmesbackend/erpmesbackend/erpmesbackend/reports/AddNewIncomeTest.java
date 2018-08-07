package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.reports;

import com.herokuapp.erpmesbackend.erpmesbackend.finance.CurrentReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddNewIncomeTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void checkIfResponseIncomeIncreased() {
        CurrentReport report = restTemplate.getForEntity("/current-report", CurrentReport.class).getBody();
        assertThat(!report.getIncome().contains(2000.00));
        ResponseEntity<CurrentReport> currentReportResponseEntity = restTemplate.postForEntity(
                "/current-report/income", 2000.00, CurrentReport.class);
        assertThat(currentReportResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(currentReportResponseEntity.getBody().getIncome().contains(2000.00));
    }
}

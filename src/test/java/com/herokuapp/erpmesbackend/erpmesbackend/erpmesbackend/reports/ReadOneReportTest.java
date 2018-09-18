package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.reports;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.MonthlyReport;
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
public class ReadOneReportTest extends FillBaseTemplate {

    @Test
    public void checkIfResponseContainsReportWithGivenId() {
        setupToken();
        ResponseEntity<MonthlyReport> forEntity = restTemplate.exchange("/reports/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), MonthlyReport.class, 3);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(forEntity.getBody().getId()).isEqualTo(3);
    }
}

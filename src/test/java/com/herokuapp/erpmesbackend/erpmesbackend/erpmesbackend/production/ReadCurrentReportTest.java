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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadCurrentReportTest extends FillBaseTemplate {

    @Test
    public void checkIfResponseContainsCurrentReport() {
        setupToken();
        ResponseEntity<CurrentReport> forEntity = restTemplate.exchange("/current-report", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), CurrentReport.class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(forEntity.getBody().getStartDate().getMonth())
                .isEqualTo(LocalDate.now().getMonth());
    }
}

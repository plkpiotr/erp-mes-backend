package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.CurrentReport;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Expense;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.ExpenseType;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.ExpenseRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportsControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
    }

    @Test
    public void readCurrentReportTest() {
        ResponseEntity<CurrentReport> forEntity = restTemplate.exchange("/current-report", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), CurrentReport.class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(forEntity.getBody().getStartDate().getMonth())
                .isEqualTo(LocalDate.now().getMonth());
    }

    @Test
    public void addIncomeTest() {
        CurrentReport report = restTemplate.exchange("/current-report", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), CurrentReport.class).getBody();
        List<Double> income = report.getIncome();
        ResponseEntity<CurrentReport> currentReportResponseEntity = restTemplate.postForEntity(
                "/current-report/income", new HttpEntity<>(2000.00, requestHeaders), CurrentReport.class);
        assertThat(currentReportResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(currentReportResponseEntity.getBody().getIncome().size() - income.size()).isEqualTo(1);
    }

    @Test
    public void addExpenseTest() {
        CurrentReport report = restTemplate.exchange("/current-report", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), CurrentReport.class).getBody();
        List<Expense> expenses = report.getExpenses();
        ExpenseRequest request = new ExpenseRequest(ExpenseType.RENT, 5000.00);
        ResponseEntity<CurrentReport> currentReportResponseEntity = restTemplate.postForEntity(
                "/current-report/expense", new HttpEntity<>(request, requestHeaders), CurrentReport.class);
        assertThat(currentReportResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(currentReportResponseEntity.getBody().getExpenses().size() - expenses.size()).isEqualTo(1);
    }
}

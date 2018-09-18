package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.reports;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.CurrentReport;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.ExpenseType;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.MonthlyReport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.Month;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SaveMonthlyReportTest extends FillBaseTemplate {

    @Test
    public void checkIfTaxesWerePaid() {
        setupToken();
        ResponseEntity<MonthlyReport> forEntity = restTemplate.exchange("/reports/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), MonthlyReport.class, 6);
        assertThat(forEntity.getBody().getStartDate().getMonth()).isEqualTo(Month.JULY);
        assertThat(forEntity.getBody().getExpenses()
                .stream()
                .filter(expense -> expense.getExpenseType().equals(ExpenseType.TAXES))
                .collect(Collectors.toList())
                .size()).isEqualTo(1);
    }

    @Test
    public void checkIfSalariesWerePaid() {
        setupToken();
        ResponseEntity<CurrentReport> forEntity = restTemplate.exchange("/current-report", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), CurrentReport.class);
        assertThat(forEntity.getBody().getStartDate().getMonth()).isEqualTo(LocalDate.now().getMonth());
        assertThat(forEntity.getBody().getExpenses()
                .stream()
                .filter(expense -> expense.getExpenseType().equals(ExpenseType.SALARIES))
                .collect(Collectors.toList())
                .size()).isEqualTo(1);
    }
}

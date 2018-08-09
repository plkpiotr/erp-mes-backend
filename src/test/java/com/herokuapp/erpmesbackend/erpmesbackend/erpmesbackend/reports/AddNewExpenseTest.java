package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.reports;

import com.herokuapp.erpmesbackend.erpmesbackend.finance.CurrentReport;
import com.herokuapp.erpmesbackend.erpmesbackend.finance.ExpenseRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.finance.ExpenseType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddNewExpenseTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void checkIfExpensesIncreased() {
        ExpenseRequest request = new ExpenseRequest(ExpenseType.RENT, 5000.00);
        CurrentReport report = restTemplate.getForEntity("/current-report", CurrentReport.class).getBody();
        ResponseEntity<CurrentReport> currentReportResponseEntity = restTemplate.postForEntity(
                "/current-report/expense", request, CurrentReport.class);
        assertThat(currentReportResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(currentReportResponseEntity.getBody()
                .getExpenses()
                .stream()
                .filter(expense -> expense.getExpenseType().equals(ExpenseType.RENT))
                .filter(expense -> expense.getAmount() == 5000.00)
                .collect(Collectors.toList())
                .size()).isEqualTo(1);
        assertThat(report.getExpenses().stream()
                .filter(expense -> expense.getExpenseType().equals(ExpenseType.RENT))
                .filter(expense -> expense.getAmount() == 5000.00)
                .collect(Collectors.toList())
                .size()).isEqualTo(0);
    }
}

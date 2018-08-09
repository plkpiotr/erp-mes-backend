package com.herokuapp.erpmesbackend.erpmesbackend.finance;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
public class ReportsController {

    @Autowired
    CurrentReportRepository currentReportRepository;

    @Autowired
    MonthlyReportRepository monthlyReportRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    EstimatedCostsRepository estimatedCostsRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    //TODO: setup only once when the app is up and running, then comment out
    @PostConstruct
    public void init() {
        EstimatedCosts estimatedCosts = new EstimatedCosts();
        estimatedCostsRepository.save(estimatedCosts);
        CurrentReport currentReport = new CurrentReport(estimatedCosts);
        currentReportRepository.save(currentReport);

        //this is only for testing purposes!!!
        Month months[] = {Month.FEBRUARY, Month.MARCH, Month.APRIL, Month.MAY, Month.JUNE};
        for (Month month : months) {
            currentReport.setStartDate(LocalDate.of(2018, month, 1));
            Expense expense = new Expense(ExpenseType.SALARIES, 120000.00);
            currentReport.addExpense(expense);
            expenseRepository.save(expense);
            currentReport.addIncome(50000.00);
            MonthlyReport monthlyReport = new MonthlyReport(currentReport);
            monthlyReport.setIncome(new ArrayList<>(currentReport.getIncome()));
            monthlyReport.setExpenses(new ArrayList<>(currentReport.getExpenses()));
            monthlyReportRepository.save(monthlyReport);
            currentReport.clearReport();
        }
        //only for saving tests
        currentReport.setStartDate(LocalDate.of(2018, Month.JULY, 1));
        currentReportRepository.save(currentReport);
    }

    @GetMapping("/reports")
    @ResponseStatus(HttpStatus.OK)
    public List<MonthlyReport> getReports() {
        if (shouldSaveReport()) {
            saveReport();
        }
        return monthlyReportRepository.findAll();
    }

    @GetMapping("/reports/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MonthlyReport getReport(@PathVariable("id") long id) {
        if (shouldSaveReport()) {
            saveReport();
        }
        checkIfReportExists(id);
        return monthlyReportRepository.findById(id).get();
    }

    @GetMapping("/current-report")
    @ResponseStatus(HttpStatus.OK)
    public CurrentReport getCurrentReport() {
        if (shouldSaveReport()) {
            saveReport();
        }
        return currentReportRepository.findById((long) 1).get();
    }

    @PutMapping("/current-report")
    @ResponseStatus(HttpStatus.OK)
    public CurrentReport recalculateCosts(@RequestBody  EstimatedCostsRequest reestimatedCosts) {
        if (shouldSaveReport()) {
            saveReport();
        }
        CurrentReport currentReport = currentReportRepository.findById((long) 1).get();
        EstimatedCosts estimatedCosts = currentReport.getEstimatedCosts();
        estimatedCosts.recalculateCosts(reestimatedCosts);
        estimatedCostsRepository.save(estimatedCosts);
        currentReport.updateEstimatedCosts(estimatedCosts);
        currentReportRepository.save(currentReport);
        return currentReport;
    }

    @PostMapping("/current-report/income")
    @ResponseStatus(HttpStatus.OK)
    public CurrentReport addIncome(@RequestBody double amount) {
        if (shouldSaveReport()) {
            saveReport();
        }
        CurrentReport currentReport = currentReportRepository.findById((long) 1).get();
        currentReport.addIncome(amount);
        currentReportRepository.save(currentReport);
        return currentReport;
    }

    @PostMapping("/current-report/expense")
    @ResponseStatus(HttpStatus.OK)
    public CurrentReport addIncome(@RequestBody ExpenseRequest request) {
        if (shouldSaveReport()) {
            saveReport();
        }
        CurrentReport currentReport = currentReportRepository.findById((long) 1).get();
        Expense expense = request.extractExpense();
        expenseRepository.save(expense);
        currentReport.addExpense(expense);
        currentReportRepository.save(currentReport);
        return currentReport;
    }

    private boolean shouldSaveReport() {
        CurrentReport currentReport = currentReportRepository.findById((long) 1).get();
        return LocalDate.now().getMonth() != currentReport.getStartDate().getMonth() &&
                !isAlreadySaved(currentReport);
    }

    private boolean isAlreadySaved(CurrentReport currentReport) {
        return monthlyReportRepository.findAll().stream().map(MonthlyReport::getStartDate).anyMatch(date ->
                currentReport.getStartDate().getMonth().equals(date.getMonth()) &&
                        currentReport.getStartDate().getYear() == date.getYear());
    }

    private void saveReport() {
        CurrentReport currentReport = currentReportRepository.findById((long) 1).get();
        MonthlyReport monthlyReport = new MonthlyReport(currentReport);
        monthlyReport.setIncome(new ArrayList<>(currentReport.getIncome()));
        monthlyReport.setExpenses(new ArrayList<>(currentReport.getExpenses()));
        payTaxes(monthlyReport);
        monthlyReportRepository.save(monthlyReport);
        currentReport.clearReport();
        paySalaries(currentReport);
        currentReportRepository.save(currentReport);
    }

    private void payTaxes(MonthlyReport monthlyReport) {
        double allSalaries = monthlyReport.getExpenses().stream()
                .filter(expense -> expense.getExpenseType().equals(ExpenseType.SALARIES))
                .map(Expense::getAmount)
                .mapToDouble(Double::doubleValue)
                .sum();
        Expense taxes = new Expense(ExpenseType.TAXES, 0.2*allSalaries+0.2*monthlyReport.getOverallIncome());
        expenseRepository.save(taxes);
        monthlyReport.payTaxes(taxes);
    }

    private void paySalaries(CurrentReport currentReport) {
        double salaries = employeeRepository.findAll().stream()
                .map(employee -> employee.getContract().getSalary())
                .mapToDouble(Double::doubleValue)
                .sum();
        Expense expense = new Expense(ExpenseType.SALARIES, salaries);
        expenseRepository.save(expense);
        currentReport.addExpense(expense);
    }

    private void checkIfReportExists(long id) {
        if(!monthlyReportRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such report doesn't exist!");
        }
    }
}

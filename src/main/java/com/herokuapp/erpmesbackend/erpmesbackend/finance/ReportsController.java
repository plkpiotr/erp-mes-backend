package com.herokuapp.erpmesbackend.erpmesbackend.finance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class ReportsController {

    @Autowired
    private CurrentReportRepository currentReportRepository;

    @Autowired
    private MonthlyReportRepository monthlyReportRepository;

    @Autowired
    private EstimatedCostsRepository estimatedCostsRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ReportService reportService;

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
        if (reportService.shouldSaveReport()) {
            reportService.saveReport();
        }
        return monthlyReportRepository.findAll();
    }

    @GetMapping("/reports/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MonthlyReport getReport(@PathVariable("id") long id) {
        if (reportService.shouldSaveReport()) {
            reportService.saveReport();
        }
        reportService.checkIfReportExists(id);
        return monthlyReportRepository.findById(id).get();
    }

    @GetMapping("/current-report")
    @ResponseStatus(HttpStatus.OK)
    public CurrentReport getCurrentReport() {
        if (reportService.shouldSaveReport()) {
            reportService.saveReport();
        }
        return currentReportRepository.findById((long) 1).get();
    }

    @PutMapping("/current-report")
    @ResponseStatus(HttpStatus.OK)
    public CurrentReport recalculateCosts(@RequestBody EstimatedCostsRequest reestimatedCosts) {
        if (reportService.shouldSaveReport()) {
            reportService.saveReport();
        }
        return reportService.recalculateCosts(reestimatedCosts);
    }

    @PostMapping("/current-report/income")
    @ResponseStatus(HttpStatus.OK)
    public CurrentReport addIncome(@RequestBody double amount) {
        if (reportService.shouldSaveReport()) {
            reportService.saveReport();
        }
        return reportService.addIncome(amount);
    }

    @PostMapping("/current-report/expense")
    @ResponseStatus(HttpStatus.OK)
    public CurrentReport addExpense(@RequestBody ExpenseRequest request) {
        if (reportService.shouldSaveReport()) {
            reportService.saveReport();
        }
        return reportService.addExpense(request);
    }

    @GetMapping("current-report/recommended-recalculations")
    @ResponseStatus(HttpStatus.OK)
    public EstimatedCostsRequest countRecommendations() {
        return reportService.countRecommendations();
    }
}

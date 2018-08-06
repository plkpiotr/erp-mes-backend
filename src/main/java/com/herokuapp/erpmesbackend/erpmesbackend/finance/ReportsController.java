package com.herokuapp.erpmesbackend.erpmesbackend.finance;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@RestController
public class ReportsController {

    @Autowired
    CurrentReportRepository currentReportRepository;

    @Autowired
    MonthlyReportRepository monthlyReportRepository;

    @Autowired
    ExpenseRepository expenseRepository;

    @Autowired
    EstimatedCostsRepository estimatedCostsRepository;

    //TODO: setup only once when the app is up and running, then comment out
    @PostConstruct
    public void init() {
        EstimatedCosts estimatedCosts = new EstimatedCosts();
        estimatedCostsRepository.save(estimatedCosts);
        CurrentReport currentReport = new CurrentReport(estimatedCosts);
        currentReportRepository.save(currentReport);
    }

    @GetMapping("/reports")
    public List<MonthlyReport> getReports() {
        if (shouldSaveReport()) {
            saveReport();
        }
        return monthlyReportRepository.findAll();
    }

    @GetMapping("/reports/{id}")
    public MonthlyReport getReport(@PathVariable("id") long id) {
        if (shouldSaveReport()) {
            saveReport();
        }
        checkIfReportExists(id);
        return monthlyReportRepository.findById(id).get();
    }

    @PutMapping("/current-report")
    public EstimatedCosts recalculateCosts(@RequestBody  EstimatedCostsRequest reestimatedCosts) {
        if (shouldSaveReport()) {
            saveReport();
        }
        CurrentReport currentReport = currentReportRepository.findById((long) 1).get();
        EstimatedCosts estimatedCosts = currentReport.getEstimatedCosts();
        estimatedCosts.recalculateCosts(reestimatedCosts);
        estimatedCostsRepository.save(estimatedCosts);
        currentReport.updateEstimatedCosts(estimatedCosts);
        currentReportRepository.save(currentReport);
        return estimatedCosts;
    }

    @GetMapping("/current-report")
    public CurrentReport getCurrentReport() {
        if (shouldSaveReport()) {
            saveReport();
        }
        return currentReportRepository.findById((long) 1).get();
    }

    @PostMapping("/current-report/income")
    public CurrentReport addIncome(@RequestParam("amount") double amount) {
        if (shouldSaveReport()) {
            saveReport();
        }
        CurrentReport currentReport = currentReportRepository.findById((long) 1).get();
        currentReport.addIncome(amount);
        currentReportRepository.save(currentReport);
        return currentReport;
    }

    @PostMapping("/current-report/expense")
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
        monthlyReportRepository.save(new MonthlyReport(currentReport));
        currentReport.clearReport();
        currentReportRepository.save(currentReport);
    }

    private void checkIfReportExists(long id) {
        if(!monthlyReportRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such report doesn't exist!");
        }
    }
}

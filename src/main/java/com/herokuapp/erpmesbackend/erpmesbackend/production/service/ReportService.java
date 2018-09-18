package com.herokuapp.erpmesbackend.erpmesbackend.production.service;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.*;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.CurrentReportRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.EstimatedCostsRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.ExpenseRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.repository.MonthlyReportRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.EstimatedCostsRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.ExpenseRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Contract;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.ContractRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final CurrentReportRepository currentReportRepository;
    private final MonthlyReportRepository monthlyReportRepository;
    private final EmployeeRepository employeeRepository;
    private final ExpenseRepository expenseRepository;
    private final EstimatedCostsRepository estimatedCostsRepository;
    private final ContractRepository contractRepository;

    @Autowired
    public ReportService(CurrentReportRepository currentReportRepository,
                         MonthlyReportRepository monthlyReportRepository, EmployeeRepository employeeRepository,
                         ExpenseRepository expenseRepository, EstimatedCostsRepository estimatedCostsRepository,
                         ContractRepository contractRepository) {
        this.currentReportRepository = currentReportRepository;
        this.monthlyReportRepository = monthlyReportRepository;
        this.employeeRepository = employeeRepository;
        this.expenseRepository = expenseRepository;
        this.estimatedCostsRepository = estimatedCostsRepository;
        this.contractRepository = contractRepository;
    }

    public boolean shouldSaveReport() {
        CurrentReport currentReport = getCurrentReport();
        return LocalDate.now().getMonth() != currentReport.getStartDate().getMonth() &&
                !isAlreadySaved(currentReport);
    }

    private boolean isAlreadySaved(CurrentReport currentReport) {
        return monthlyReportRepository.findAll().stream().map(MonthlyReport::getStartDate).anyMatch(date ->
                currentReport.getStartDate().getMonth().equals(date.getMonth()) &&
                        currentReport.getStartDate().getYear() == date.getYear());
    }

    public void saveReport() {
        CurrentReport currentReport = getCurrentReport();
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
        Expense taxes = new Expense(ExpenseType.TAXES, 0.2 * allSalaries + 0.2 * monthlyReport.getOverallIncome());
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

    public void checkIfReportExists(long id) {
        if (!monthlyReportRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such report doesn't exist!");
        }
    }

    public CurrentReport recalculateCosts(EstimatedCostsRequest request) {
        CurrentReport currentReport = getCurrentReport();
        EstimatedCosts estimatedCosts = currentReport.getEstimatedCosts();
        estimatedCosts.recalculateCosts(request);
        estimatedCostsRepository.save(estimatedCosts);
        currentReport.updateEstimatedCosts(estimatedCosts);
        currentReportRepository.save(currentReport);
        return currentReport;
    }

    public CurrentReport addIncome(double amount) {
        CurrentReport currentReport = getCurrentReport();
        currentReport.addIncome(amount);
        currentReportRepository.save(currentReport);
        return currentReport;
    }

    public CurrentReport addExpense(ExpenseRequest request) {
        CurrentReport currentReport = getCurrentReport();
        Expense expense = request.extractExpense();
        expenseRepository.save(expense);
        currentReport.addExpense(expense);
        currentReportRepository.save(currentReport);
        return currentReport;
    }

    private double getNewIncome(List<MonthlyReport> allReports) {
        double newIncomeValue;
        double oldIncomeValue = getCurrentReport().getEstimatedCosts().getEstimatedIncome();
        double meanIncome = allReports.stream()
                .map(MonthlyReport::getOverallIncome)
                .mapToDouble(Double::doubleValue)
                .sum() / allReports.size();
        if (meanIncome >= oldIncomeValue) {
            newIncomeValue = meanIncome / oldIncomeValue >= 1.2 ?
                    1.1 * oldIncomeValue : oldIncomeValue;
        } else {
            newIncomeValue = oldIncomeValue / meanIncome >= 1.15 ?
                    0.9 * oldIncomeValue : oldIncomeValue;
        }
        return newIncomeValue;
    }

    private double getNewValue(List<MonthlyReport> allReports, ExpenseType expenseType, double oldValue) {
        double newValue;
        List<Double> meanValues = new ArrayList<>();
        allReports.forEach(report -> {
            List<Expense> collect = report.getExpenses().stream()
                    .filter(expense -> expense.getExpenseType().equals(expenseType))
                    .collect(Collectors.toList());
            if (collect.size() > 0) {
                meanValues.add(collect.stream()
                        .map(Expense::getAmount)
                        .mapToDouble(Double::doubleValue)
                        .sum() / collect.size());
            }
        });
        if (meanValues.size() == 0) {
            newValue = oldValue;
        } else {
            double mean = meanValues.stream()
                    .mapToDouble(Double::doubleValue)
                    .sum() / meanValues.size();
            if (mean >= oldValue) {
                newValue = mean / oldValue >= 1.15 ?
                        1.1 * oldValue : oldValue;
            } else {
                newValue = oldValue / mean >= 1.2 ?
                        0.9 * oldValue : oldValue;
            }
        }
        return newValue;
    }

    private CurrentReport getCurrentReport() {
        return currentReportRepository.findById((long) 1).get();
    }

    public EstimatedCostsRequest countRecommendations() {
        CurrentReport currentReport = getCurrentReport();
        List<MonthlyReport> allReports = monthlyReportRepository.findAll();

        if (allReports.size() >= 5) {
            double newIncomeValue = getNewIncome(allReports);
            double newShippingValue = getNewValue(allReports, ExpenseType.SHIPPING, currentReport
                    .getEstimatedCosts().getEstimatedShippingCosts());
            double newBillsValue = getNewValue(allReports, ExpenseType.BILLS, currentReport.getEstimatedCosts()
                    .getEstimatedBills());
            double newRentValue = getNewValue(allReports, ExpenseType.RENT, currentReport
                    .getEstimatedCosts().getRent());
            double newSalariesValue = contractRepository.findAll().size() > 0 ?
                    contractRepository.findAll().stream()
                            .map(Contract::getSalary)
                            .mapToDouble(Double::doubleValue)
                            .sum() : 0;
            double newStockValue = getNewValue(allReports, ExpenseType.STOCK, currentReport
                    .getEstimatedCosts().getStockCosts());
            double newSocialFundValue = getNewValue(allReports, ExpenseType.SOCIAL_FUND, currentReport
                    .getEstimatedCosts().getSocialFund());
            double newUnexpectedValue = getNewValue(allReports, ExpenseType.UNEXPECTED, currentReport
                    .getEstimatedCosts().getUnexpected());

            return new EstimatedCostsRequest(newIncomeValue, newShippingValue, newBillsValue, newRentValue,
                    newSalariesValue, newStockValue, newSocialFundValue, newUnexpectedValue);
        } else {
            EstimatedCosts estimatedCosts = currentReport.getEstimatedCosts();
            return new EstimatedCostsRequest(estimatedCosts.getEstimatedIncome(),
                    estimatedCosts.getEstimatedShippingCosts(), estimatedCosts.getEstimatedBills(),
                    estimatedCosts.getRent(), estimatedCosts.getSalaries(), estimatedCosts.getStockCosts(),
                    estimatedCosts.getSocialFund(), estimatedCosts.getUnexpected());
        }
    }
}

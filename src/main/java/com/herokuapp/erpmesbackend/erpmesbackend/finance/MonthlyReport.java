package com.herokuapp.erpmesbackend.erpmesbackend.finance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class MonthlyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToMany
    private List<Expense> expenses;

    @ElementCollection
    private List<Double> income;

    private LocalDate startDate;

    private double overallExpenses;
    private double overallIncome;
    private double balance;

    @OneToOne
    private EstimatedCosts estimatedCosts;

    public MonthlyReport(CurrentReport currentReport) {
        estimatedCosts = currentReport.getEstimatedCosts();
        expenses = new ArrayList<>();
        income = new ArrayList<>();
        startDate = currentReport.getStartDate();
        updateIncome();
        updateExpenses();
        balance = overallIncome - overallExpenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        updateExpenses();
    }

    public void setIncome(List<Double> income) {
        this.income = income;
        updateIncome();
    }

    private void updateIncome() {
        overallIncome = income.stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        balance = overallIncome - overallExpenses;
    }

    private void updateExpenses() {
        overallExpenses = expenses.stream()
                .map(Expense::getAmount)
                .mapToDouble(Double::doubleValue)
                .sum();
        balance = overallIncome - overallExpenses;
    }

    public void payTaxes(Expense taxes) {
        expenses.add(taxes);
        updateExpenses();
    }
}

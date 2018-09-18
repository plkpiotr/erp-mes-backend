package com.herokuapp.erpmesbackend.erpmesbackend.production.request;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Expense;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.ExpenseType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {

    @NotNull
    private ExpenseType expenseType;

    @NotNull
    private double amount;

    public Expense extractExpense() {
        return new Expense(expenseType, amount);
    }
}

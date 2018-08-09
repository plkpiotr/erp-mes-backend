package com.herokuapp.erpmesbackend.erpmesbackend.finance;

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

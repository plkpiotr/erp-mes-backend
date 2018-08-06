package com.herokuapp.erpmesbackend.erpmesbackend.finance;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    private double amount;

    public Expense(ExpenseType expenseType, double amount) {
        this.expenseType = expenseType;
        this.amount = amount;
    }
}

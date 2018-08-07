package com.herokuapp.erpmesbackend.erpmesbackend.contracts;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    public Contract(String accountNumber, int daysOffPerYear, double salary) {
        this.accountNumber = accountNumber;
        this.daysOffPerYear = daysOffPerYear;
        this.salary = salary;
    }

    private String accountNumber;
    private int daysOffPerYear;
    private double salary;

    public boolean checkIfDataEquals(Contract contract) {
        return accountNumber.equals(contract.getAccountNumber()) &&
                daysOffPerYear == contract.getDaysOffPerYear() &&
                salary == contract.getSalary();
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.staff.model;

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

    private String accountNumber;
    private int daysOffPerYear;
    private double salary;

    public Contract(String accountNumber, int daysOffPerYear, double salary) {
        this.accountNumber = accountNumber;
        this.daysOffPerYear = daysOffPerYear;
        this.salary = salary;
    }
}

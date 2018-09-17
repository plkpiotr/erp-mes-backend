package com.herokuapp.erpmesbackend.erpmesbackend.planning;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class DailyPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int employeesPerDay;
    private int ordersPerDay;
    private int returnsPerDay;
    private int complaintsResolvedPerDay;

    public DailyPlan() {
        this.employeesPerDay = 5;
        this.ordersPerDay = 15;
        this.returnsPerDay = 5;
        this.complaintsResolvedPerDay = 5;
    }
}

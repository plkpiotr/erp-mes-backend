package com.herokuapp.erpmesbackend.erpmesbackend.finance;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
public class EstimatedCosts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private double estimatedIncome;
    private double estimatedShippingCosts;
    private double estimatedBills;
    private double rent;
    private double salaries;
    private double stockCosts;
    private double socialFund;
    private double unexpected;
    private double taxes;

    public EstimatedCosts() {
        estimatedIncome = 175000.00;
        estimatedShippingCosts = 5000.00;
        estimatedBills = 3000.00;
        rent = 10000.00;
        salaries = 100000.00;
        stockCosts = 30000.00;
        socialFund = 2000.00;
        unexpected = 5000.00;
        taxes = 0.18 * (estimatedIncome + salaries);
    }

    public void recalculateCosts(EstimatedCostsRequest reestimatedCosts) {
        this.estimatedIncome = reestimatedCosts.getEstimatedIncome();
        this.estimatedShippingCosts = reestimatedCosts.getEstimatedShippingCosts();
        this.estimatedBills = reestimatedCosts.getEstimatedBills();
        this.rent = reestimatedCosts.getRent();
        this.salaries = reestimatedCosts.getSalaries();
        this.stockCosts = reestimatedCosts.getStockCosts();
        this.socialFund = reestimatedCosts.getSocialFund();
        this.unexpected = reestimatedCosts.getUnexpected();
        taxes = 0.18 * (estimatedIncome + salaries);
    }
}

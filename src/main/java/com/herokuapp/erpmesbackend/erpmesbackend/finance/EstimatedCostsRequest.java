package com.herokuapp.erpmesbackend.erpmesbackend.finance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstimatedCostsRequest {

    @NotNull
    private double estimatedIncome;

    @NotNull
    private double estimatedShippingCosts;

    @NotNull
    private double estimatedBills;

    @NotNull
    private double rent;

    @NotNull
    private double salaries;

    @NotNull
    private double stockCosts;

    @NotNull
    private double socialFund;

    @NotNull
    private double unexpected;
}

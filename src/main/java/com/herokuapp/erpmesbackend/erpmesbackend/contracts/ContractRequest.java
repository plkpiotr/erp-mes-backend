package com.herokuapp.erpmesbackend.erpmesbackend.contracts;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractRequest {

    @NotNull
    @Pattern(regexp = "[0-9]{26}")
    private String accountNumber;

    @NotNull
    private int daysOffPerYear;

    @NotNull
    private double salary;

    public Contract extractContract() {
        return new Contract(accountNumber, daysOffPerYear, salary);
    }

}

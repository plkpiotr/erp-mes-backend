package com.herokuapp.erpmesbackend.erpmesbackend.planning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyPlanRequest {

    @NotNull
    private int employeesPerDay;

    @NotNull
    private int ordersPerDay;

    @NotNull
    private int returnsPerDay;

    @NotNull
    private int complaintsResolvedPerDay;
}

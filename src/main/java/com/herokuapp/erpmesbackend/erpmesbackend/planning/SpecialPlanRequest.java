package com.herokuapp.erpmesbackend.erpmesbackend.planning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpecialPlanRequest {

    @NotNull
    private String description;

    @NotNull
    private LocalDate day;

    @NotNull
    private int employeesPerDay;

    @NotNull
    private int ordersPerDay;

    @NotNull
    private int returnsPerDay;

    @NotNull
    private int complaintsResolvedPerDay;

    public SpecialPlan extractSpecialPlan() {
        return new SpecialPlan(description, day, employeesPerDay, ordersPerDay, returnsPerDay,
                complaintsResolvedPerDay);
    }
}

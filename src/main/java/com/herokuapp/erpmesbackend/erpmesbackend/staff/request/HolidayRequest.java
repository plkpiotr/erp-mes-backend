package com.herokuapp.erpmesbackend.erpmesbackend.staff.request;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.HolidayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HolidayRequest {

    @NotNull
    private LocalDate startDate;

    @NotNull
    private int duration;

    @NotNull
    private HolidayType holidayType;

}

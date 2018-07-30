package com.herokuapp.erpmesbackend.erpmesbackend.holidays;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
public class Holiday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDate startDate;
    private int duration;

    @Enumerated(EnumType.STRING)
    private HolidayType holidayType;

    ApprovalState approvalState;

    public Holiday(LocalDate startDate, int duration, HolidayType holidayType) {
        this.startDate = startDate;
        this.duration = duration;
        this.holidayType = holidayType;
        this.approvalState = holidayType.equals(HolidayType.VACATION) ?
                ApprovalState.PENDING : ApprovalState.APPROVED;
    }

    public void approve() {
        approvalState = ApprovalState.APPROVED;
    }

    public void decline() {
        approvalState = ApprovalState.DECLINED;
    }

    public LocalDate calculateEndDate() {
        return startDate.plusDays(duration - 1);
    }

    public int calculateWorkingDaysOff() {
        int days = 0;
        for (int i = 0; i < duration; i++) {
            if (startDate.plusDays(i).getDayOfWeek() != DayOfWeek.SUNDAY &&
                    startDate.plusDays(i).getDayOfWeek() != DayOfWeek.SATURDAY) {
                days++;
            }
        }
        return days;
    }

    public boolean checkIfDataEquals(Holiday holiday) {
        return startDate.equals(holiday.getStartDate()) &&
                duration == holiday.getDuration() &&
                approvalState.equals(holiday.getApprovalState());
    }
}

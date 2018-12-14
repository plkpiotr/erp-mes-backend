package com.herokuapp.erpmesbackend.erpmesbackend.production.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Indicators {

    private long numberTasksEmployee;
    private long numberTasksEverybody;

    private long numberTasksEmployeeToDo;
    private long numberTasksEverybodyToDo;
    private long numberTasksEmployeeDoing;
    private long numberTasksEverybodyDoing;
    private long numberTasksEmployeeDone;
    private long numberTasksEverybodyDone;

    private Long numberTasksEmployeeDoneBeforeDeadline;
    private Long numberTasksEverybodyDoneBeforeDeadline;

    private long numberSuggestionsEmployee;
    private long numberSuggestionsEverybody;

    private long numberSuggestionsEmployeeReported;
    private long numberSuggestionsEverybodyReported;
    private long numberSuggestionsEmployeeInImplementation;
    private long numberSuggestionsEverybodyInImplementation;
    private long numberSuggestionsEmployeeImplemented;
    private long numberSuggestionsEverybodyImplemented;

    private long numberNotificationsAsTransferee;
    private long numberNotificationsAsConsignee;

    private Long averageTimeTasksEmployeeBetweenDeadlineAndEndTime;
    private Long averageTimeTasksEverybodyBetweenDeadlineAndEndTime;

    private Long averageTimeNotificationsEmployeeBetweenStartTimeAndCreationTime;
    private Long averageTimeNotificationsEverybodyBetweenStartTimeAndCreationTime;

    private Long averageTimeNotificationsEmployeeBetweenEndTimeAndStartTime;
    private Long averageTimeNotificationsEverybodyBetweenEndTimeAndStartTime;
}

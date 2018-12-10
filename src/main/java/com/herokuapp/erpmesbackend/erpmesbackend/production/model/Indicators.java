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

    private long numberTasksEmployeeDoneBeforeDeadline;
    private long numberTasksEverybodyDoneBeforeDeadline;

    private long averageTimeTasksEmployeeBetweenStartTimeAndCreationTime;
    private long averageTimeTasksEverybodyBetweenStartTimeAndCreationTime;

    private long sumTimeTasksEmployeeBetweenStartTimeAndCreationTime;
    private long sumTimeTasksEveryBodyBetweenStartTimeAndCreationTime;

    private long numberNotificationsAsTransferee;
    private long numberNotificationsAsConsignee;

    private long averageTimeNotificationsEmployeeBetweenStartTimeAndCreationTime;
    private long averageTimeNotificationsEverybodyBetweenStartTimeAndCreationTime;

    private long numberSuggestionsEmployee;
    private long numberSuggestionsEverybody;

    private long numberSuggestionsEmployeeReported;
    private long numberSuggestionsEverybodyReported;
    private long numberSuggestionsEmployeeInImplementation;
    private long numberSuggestionsEverybodyInImplementation;
    private long numberSuggestionsEmployeeImplemented;
    private long numberSuggestionsEverybodyImplemented;
}

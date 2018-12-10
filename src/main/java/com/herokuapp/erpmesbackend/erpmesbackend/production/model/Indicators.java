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

    private long averageTimeEmployeeBetweenStartTimeAndCreationTime;
    private long averageTimeEverybodyBetweenStartTimeAndCreationTime;

    private long sumTimeEmployeeBetweenStartTimeAndCreationTime
}

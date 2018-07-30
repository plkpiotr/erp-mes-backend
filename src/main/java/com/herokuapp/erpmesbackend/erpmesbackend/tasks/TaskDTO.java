package com.herokuapp.erpmesbackend.erpmesbackend.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private String name;
    private String category;
    private String assignee;
    private List<String> precedingTasks;
    private String details;
    private int estimatedTimeInMinutes;
    private String deadline;
    private String creationTime;
    private String startTime;
    private String endTime;
}


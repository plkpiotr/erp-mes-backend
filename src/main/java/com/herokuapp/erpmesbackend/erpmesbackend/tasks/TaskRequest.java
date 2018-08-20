package com.herokuapp.erpmesbackend.erpmesbackend.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NonNull
    private String name;

    @NonNull
    private Category category;

    private Long assigneeId;

    @NonNull
    private List<Long> precedingTaskIds;

    @NonNull
    private String details;

    private Integer estimatedTimeInMinutes;

    @NonNull
    private LocalDateTime deadline;

    private Type type;
    private Long reference;
    private LocalDateTime scheduledTime;
}

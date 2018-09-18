package com.herokuapp.erpmesbackend.erpmesbackend.production.request;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
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

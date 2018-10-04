package com.herokuapp.erpmesbackend.erpmesbackend.production.request;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Category;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NonNull
    private String name;

    @NonNull
    private List<Long> precedingTaskIds = new ArrayList<>();

    private Long assigneeId;

    @NonNull
    private Integer estimatedTime;

    @NonNull
    private LocalDateTime deadline;

    private LocalDateTime scheduledTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String details;
    private Type type;
}

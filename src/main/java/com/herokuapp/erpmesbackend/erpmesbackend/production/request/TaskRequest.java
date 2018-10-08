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

    private List<Long> precedingTaskIds;

    private Long assigneeId;

    @NonNull
    private Integer estimatedTime;

    @NonNull
    private LocalDateTime deadline;

    private LocalDateTime scheduledTime;
    private String details;

    @NonNull
    private Type type;
}

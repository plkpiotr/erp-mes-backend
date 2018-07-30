package com.herokuapp.erpmesbackend.erpmesbackend.tasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotNull
    private String name;

    @NonNull
    private Category category;

    @NonNull
    private Long assigneeId;

    private List<Long> precedingTasksIds;

    @NotNull
    private String details;

    private int estimatedTimeInMinutes;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime deadline;
}

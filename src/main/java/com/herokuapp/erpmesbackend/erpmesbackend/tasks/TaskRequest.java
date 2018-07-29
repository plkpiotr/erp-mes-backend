package com.herokuapp.erpmesbackend.erpmesbackend.tasks;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
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

    @NonNull
    private Category category;

    @NonNull
    private Long assigneeId;

    @NonNull
    private List<Long> precedingTasksId;

    @NonNull
    private boolean isReadyToDoing;

    @NonNull
    private int estimatedTimeInMinutes;

    @NotNull
    private LocalDateTime deadline;
}

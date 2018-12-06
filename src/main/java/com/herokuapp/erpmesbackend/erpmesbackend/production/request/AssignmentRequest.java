package com.herokuapp.erpmesbackend.erpmesbackend.production.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequest {

    @NonNull
    private List<Long> taskIds;

    @NonNull
    private List<Long> assigneeIds;

    @NonNull
    private LocalDateTime startTime;
}

package com.herokuapp.erpmesbackend.erpmesbackend.production.dto;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Category;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Task;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class TaskDTO {

    private long id;
    private String name;
    private Category category;
    private EmployeeDTO assigneeDTO;
    private List<TaskDTO> precedingTaskDTOs;
    private String details;
    private Integer estimatedTimeInMinutes;
    private LocalDateTime deadline;
    private Type type;
    private Long reference;
    private LocalDateTime scheduledTime;
    private LocalDateTime creationTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.category = task.getCategory();
        if (task.getAssignee() != null) {
            this.assigneeDTO = new EmployeeDTO(task.getAssignee());
        }
        if (task.getPrecedingTasks() != null && !task.getPrecedingTasks().isEmpty()) {
            task.getPrecedingTasks().forEach(precedingTask -> this.precedingTaskDTOs.add(new TaskDTO(precedingTask)));
        }
        this.details = task.getDetails();
        this.estimatedTimeInMinutes = task.getEstimatedTimeInMinutes();
        this.deadline = task.getDeadline();
        this.type = task.getType();
        this.reference = task.getReference();
        this.scheduledTime = task.getScheduledTime();
        this.creationTime = task.getCreationTime();
        this.startTime = task.getStartTime();
        this.endTime = task.getEndTime();
    }

    public TaskDTO(String name, EmployeeDTO assigneeDTO, List<TaskDTO> precedingTaskDTOs, String details,
                   Integer estimatedTimeInMinutes, Type type, Long reference) {
        this.name = name;
        this.assigneeDTO = assigneeDTO;
        this.details = details;
        this.precedingTaskDTOs = precedingTaskDTOs;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.type = type;
        this.reference = reference;
    }

    public boolean checkIfDataEquals(TaskDTO task) {
        return name.equals(task.getName()) &&
                comparePrecedingTaskDTOs(task.getPrecedingTaskDTOs()) &&
                details.equals(task.getDetails()) &&
                estimatedTimeInMinutes.equals(task.getEstimatedTimeInMinutes());
    }

    private boolean comparePrecedingTaskDTOs(List<TaskDTO> taskDTOList) {
        if (taskDTOList.isEmpty())
            return true;
        for (TaskDTO precedingTaskDTO : precedingTaskDTOs) {
            if (taskDTOList.stream().noneMatch(t -> t.checkIfDataEquals(precedingTaskDTO)))
                return false;
        }
        return true;
    }
}

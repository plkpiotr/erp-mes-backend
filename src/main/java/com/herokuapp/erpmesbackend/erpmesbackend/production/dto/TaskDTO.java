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
    private List<Long> precedingTaskIds;
    private EmployeeDTO authorDTO;
    private EmployeeDTO assigneeDTO;
    private LocalDateTime creationTime;
    private Integer estimatedTime;
    private LocalDateTime deadline;
    private LocalDateTime scheduledTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String details;
    private EmployeeDTO startEmployee;
    private EmployeeDTO endEmployee;
    private Type type;

    public TaskDTO(Task task) {
        this.id = task.getId();
        this.name = task.getName();
        this.category = task.getCategory();
        this.precedingTaskIds = task.getPrecedingTaskIds();
        this.authorDTO = new EmployeeDTO(task.getAuthor());
        this.estimatedTime = task.getEstimatedTime();
        this.creationTime = task.getCreationTime();
        this.deadline = task.getDeadline();

        if (task.getAssignee() != null) {
            this.assigneeDTO = new EmployeeDTO(task.getAssignee());
        }

        if (task.getScheduledTime() != null) {
            this.scheduledTime = task.getScheduledTime();
        }

        if (task.getStartTime() != null) {
            this.startTime = task.getStartTime();
        }

        if (task.getEndTime() != null) {
            this.endTime = task.getEndTime();
        }

        if (task.getDetails() != null) {
            this.details = task.getDetails();
        }

        if (task.getType() != null) {
            this.type = task.getType();
        }

        if (task.getStartTime() != null) {
            this.startTime = task.getStartTime();
            this.startEmployee = new EmployeeDTO(task.getStartEmployee());
        }

        if (task.getEndTime() != null) {
            this.endTime = task.getEndTime();
            this.endEmployee = new EmployeeDTO(task.getEndEmployee());
        }
    }

    public TaskDTO(String name, List<Long> precedingTaskIds, EmployeeDTO authorDTO, EmployeeDTO assigneeDTO,
                   Integer estimatedTime) {
        this.name = name;
        this.precedingTaskIds = precedingTaskIds;
        this.authorDTO = authorDTO;
        this.assigneeDTO = assigneeDTO;
        this.estimatedTime = estimatedTime;
    }

    public boolean checkIfDataEquals(TaskDTO task) {
        return name.equals(task.getName()) &&
                comparePrecedingTaskIds(task.getPrecedingTaskIds()) &&
                authorDTO.equals(task.getAuthorDTO()) &&
                assigneeDTO.equals(task.assigneeDTO) &&
                estimatedTime.equals(task.estimatedTime);
    }

    private boolean comparePrecedingTaskIds(List<Long> precedingTaskIdList) {
        for (Long precedingTaskId : precedingTaskIds) {
            if (precedingTaskIdList.stream().noneMatch(id -> id.equals(precedingTaskId)))
                return false;
        }
        return true;
    }
}

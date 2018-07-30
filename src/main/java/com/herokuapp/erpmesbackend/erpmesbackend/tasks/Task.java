package com.herokuapp.erpmesbackend.erpmesbackend.tasks;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Entity
@Getter
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToOne
    private Employee assignee;

    @OneToMany
    private List<Task> precedingTasks;

    private String details;
    private int estimatedTimeInMinutes;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime deadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime creationTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endTime;

    public Task(String name, Category category, Employee assignee, List<Task> precedingTasks, String details,
                int estimatedTimeInMinutes, LocalDateTime deadline) {
        this.name = name;
        this.category = category;
        this.assignee = assignee;
        this.precedingTasks = precedingTasks;
        this.details = details;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.deadline = deadline;
        this.creationTime = LocalDateTime.now();
    }

    public boolean checkIfDataEquals(Task task) {
        return name.equals(task.getName()) &&
                category.equals(task.getCategory()) &&
                //assignee.equals(task.getAssignee());
                //comparePrecedingTasks(task.getPrecedingTasks()) &&
                details.equals(task.getDetails()) &&
                estimatedTimeInMinutes == task.getEstimatedTimeInMinutes() &&
                deadline.isEqual(task.getDeadline());
                //creationTime.isEqual(task.getCreationTime());
                //startTime.isEqual(task.getStartTime()) &&
                //endTime.isEqual(task.getEndTime());
    }

    private boolean comparePrecedingTasks(List<Task> tasksList) {
        for (Task task : precedingTasks) {
            if (tasksList.stream().noneMatch(t -> t.checkIfDataEquals(task)))
                return false;
        }
        return true;
    }
}

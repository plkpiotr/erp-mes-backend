package com.herokuapp.erpmesbackend.erpmesbackend.production.model;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category = Category.TODO;

    @Column(nullable = false)
    @ElementCollection
    private List<Long> precedingTaskIds = new ArrayList<>();

    @OneToOne
    private Employee assignee;

    @Column(nullable = false)
    private LocalDateTime creationTime;

    @Column(nullable = false)
    private Integer estimatedTime;

    @Column(nullable = false)
    private LocalDateTime deadline;

    private LocalDateTime scheduledTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String details;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long reference;

    public Task(String name, List<Long> precedingTaskIds, Employee assignee, LocalDateTime scheduledTime,
                Integer estimatedTime, LocalDateTime deadline, String details, Type type, Long reference) {
        this.name = name;
        this.precedingTaskIds = precedingTaskIds;
        this.assignee = assignee;
        this.creationTime = LocalDateTime.now();
        this.scheduledTime = scheduledTime;
        this.estimatedTime = estimatedTime;
        this.deadline = deadline;
        this.details = details;
        this.type = type;
        this.reference = reference;
    }

    public boolean checkIfDataEquals(Task task) {
        return name.equals(task.getName()) &&
                comparePrecedingTaskIds(task.getPrecedingTaskIds()) &&
                estimatedTime.equals(task.getEstimatedTime());

    }

    private boolean comparePrecedingTaskIds(List<Long> precedingTaskIdList) {
        for (Long precedingTaskId : precedingTaskIds) {
            if (precedingTaskIdList.stream().noneMatch(id -> id.equals(precedingTaskId)))
                return false;
        }
        return true;
    }
}

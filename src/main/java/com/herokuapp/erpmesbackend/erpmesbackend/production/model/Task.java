package com.herokuapp.erpmesbackend.erpmesbackend.production.model;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    private Category category = Category.TO_DO;

    @ElementCollection
    private List<Long> precedingTaskIds = new ArrayList<>();

    @OneToOne
    private Employee author;

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

    @OneToOne
    private Employee startEmployee;

    @OneToOne
    private Employee endEmployee;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    public Task(String name, List<Long> precedingTaskIds, Employee author, Employee assignee,
                LocalDateTime scheduledTime, Integer estimatedTime, LocalDateTime deadline, String details, Type type) {
        this.name = name;
        this.precedingTaskIds = precedingTaskIds;
        this.author = author;
        this.assignee = assignee;
        this.creationTime = LocalDateTime.now();
        this.scheduledTime = scheduledTime;
        this.estimatedTime = estimatedTime;
        this.deadline = deadline;
        this.details = details;
        this.type = type;
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

    public static int compare(Task t1, Task t2) {
        Integer sizePrecedingTasksFirst = t1.getPrecedingTaskIds().size();
        Integer sizePrecedingTasksSecond = t2.getPrecedingTaskIds().size();
        int comparison = sizePrecedingTasksFirst.compareTo(sizePrecedingTasksSecond);

        if (comparison == 0) {
            comparison = t1.getEstimatedTime().compareTo(t2.getEstimatedTime());
        }
        return comparison;
    }
}

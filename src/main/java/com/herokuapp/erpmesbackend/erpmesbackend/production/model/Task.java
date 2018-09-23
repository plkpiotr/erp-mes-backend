package com.herokuapp.erpmesbackend.erpmesbackend.production.model;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private Category category;

    @OneToOne
    private Employee assignee;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "preceding_tasks_id")
    private List<Task> precedingTasks;

    private String details;
    private Integer estimatedTimeInMinutes;
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long reference;
    private LocalDateTime scheduledTime;

    @Column(nullable = false)
    private LocalDateTime creationTime;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(String name, Category category, Employee assignee, List<Task> precedingTasks, String details,
                Integer estimatedTimeInMinutes, LocalDateTime deadline, Type type, Long reference,
                LocalDateTime scheduledTime) {
        this.name = name;
        this.category = category;
        this.assignee = assignee;
        this.precedingTasks = precedingTasks;
        this.details = details;
        this.estimatedTimeInMinutes = estimatedTimeInMinutes;
        this.deadline = deadline;
        this.reference = reference;
        this.scheduledTime = scheduledTime;
        this.type = type;
        this.creationTime = LocalDateTime.now();
        this.startTime = null;
        this.endTime = null;
    }

    public boolean checkIfDataEquals(Task task) {
        return name.equals(task.getName()) &&
                category.equals(task.getCategory()) &&
                // assignee.checkIfDataEquals(task.getAssignee()) &&
                comparePrecedingTasks(task.getPrecedingTasks()) &&
                details.equals(task.getDetails()) &&
                // estimatedTimeInMinutes.equals(task.getEstimatedTimeInMinutes()) &&
                deadline.isEqual(task.getDeadline());
    }

    private boolean comparePrecedingTasks(List<Task> taskList) {
        if (taskList.isEmpty())
            return true;
        for (Task task : precedingTasks) {
            if (taskList.stream().noneMatch(t -> t.checkIfDataEquals(task)))
                return false;
        }
        return true;
    }
}

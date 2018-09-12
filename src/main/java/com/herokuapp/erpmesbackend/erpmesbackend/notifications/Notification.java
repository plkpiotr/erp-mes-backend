package com.herokuapp.erpmesbackend.erpmesbackend.notifications;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.Type;
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
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    @Column(nullable = false)
    private String instruction;

    private String description;

    @OneToOne
    private Employee notifier;

    @OneToOne
    private Employee transferee;

    @Column(nullable = false)
    @ManyToMany
    private List<Employee> consignees;

    @Column(nullable = false)
    private LocalDateTime creationTime;

    @Enumerated(EnumType.STRING)
    private Type type;

    private Long reference;

    public Notification(String instruction, String description, Employee notifier, List<Employee> consignees, Type type,
                        Long reference) {
        this.state = State.REPORTED;
        this.instruction = instruction;
        this.description = description;
        this.notifier = notifier;
        this.transferee = null;
        this.consignees = consignees;
        this.creationTime = LocalDateTime.now();
        this.type = type;
        this.reference = reference;
    }

    public boolean checkIfDataEquals(Notification notification) {
        return state.equals(notification.getState()) &&
                instruction.equals(notification.getInstruction()) &&
                description.equals(notification.getDescription()) &&
                notifier.checkIfDataEquals(notification.getNotifier()) &&
                compareConsignees(notification.getConsignees());
    }

    private boolean compareConsignees(List<Employee> consigneeList) {
        if (consigneeList.isEmpty())
            return true;
        for (Employee employee : consignees) {
            if (consigneeList.stream().noneMatch(t -> t.checkIfDataEquals(employee)))
                return false;
        }
        return true;
    }
}

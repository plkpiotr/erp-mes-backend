package com.herokuapp.erpmesbackend.erpmesbackend.notifications;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    @OneToMany
    //@JoinColumn(name = "consignees_id")
    private List<Employee> consignees;

    public Notification(State state, String instruction, String description, Employee notifier, Employee transferee,
                        List<Employee> consignees) {
        this.state = state;
        this.instruction = instruction;
        this.description = description;
        this.notifier = notifier;
        this.transferee = transferee;
        this.consignees = consignees;
    }

    public boolean checkIfDataEquals(Notification notification) {
        return state.equals(notification.getState()) &&
                instruction.equals(notification.instruction) &&
                description.equals(notification.description) &&
                notifier.checkIfDataEquals(notification.notifier) &&
                transferee.checkIfDataEquals(notification.transferee) &&
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

package com.herokuapp.erpmesbackend.erpmesbackend.notifications;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.orders.Order;
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
    private Order order;

    @OneToOne
    private Employee notifier;

    @OneToOne
    private Employee transferee;

    @Column(nullable = false)
    @OneToMany
    //@JoinColumn(name = "consignees_id")
    private List<Employee> consignees;

    @Column(nullable = false)
    private LocalDateTime creationTime;

    public Notification(State state, String instruction, String description, Order order, Employee notifier,
                        List<Employee> consignees) {
        this.state = state;
        this.instruction = instruction;
        this.description = description;
        this.order = order;
        this.notifier = notifier;
        this.transferee = null;
        this.consignees = consignees;
        this.creationTime = LocalDateTime.now();
    }

    public boolean checkIfDataEquals(Notification notification) {
        return state.equals(notification.getState()) &&
                instruction.equals(notification.getInstruction()) &&
                description.equals(notification.getDescription()) &&
                order.checkIfDataEquals(notification.getOrder()) &&
                notifier.checkIfDataEquals(notification.getNotifier()) &&
                transferee.checkIfDataEquals(notification.getTransferee()) &&
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

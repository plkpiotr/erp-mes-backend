package com.herokuapp.erpmesbackend.erpmesbackend.communication.model;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @OneToOne
    private Employee endEmployee;

    public Notification(String instruction, String description, Employee notifier, List<Employee> consignees,
                        Type type) {
        this.state = State.REPORTED;
        this.instruction = instruction;
        this.description = description;
        this.notifier = notifier;
        this.transferee = null;
        this.consignees = consignees;
        this.creationTime = LocalDateTime.now();
        this.type = type;
    }
}

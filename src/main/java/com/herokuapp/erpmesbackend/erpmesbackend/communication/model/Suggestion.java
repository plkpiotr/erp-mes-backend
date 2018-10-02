package com.herokuapp.erpmesbackend.erpmesbackend.communication.model;

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
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Phase phase;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @OneToOne
    private Employee author;

    @Column(nullable = false)
    @ManyToMany
    private List<Employee> recipients;

    @Column(nullable = false)
    private LocalDateTime creationTime;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @OneToOne
    private Employee startEmployee;

    @OneToOne
    private Employee endEmployee;

    public Suggestion(String name, String description, Employee author, List<Employee> recipients) {
        this.phase = Phase.REPORTED;
        this.name = name;
        this.description = description;
        this.author = author;
        this.recipients = recipients;
        this.creationTime = LocalDateTime.now();
    }

    public boolean checkIfDataEquals(Suggestion suggestion) {
        return name.equals(suggestion.getName()) &&
                phase.equals(suggestion.getPhase()) &&
                description.equals(suggestion.getDescription()) &&
                // author.checkIfDataEquals(suggestion.getAuthor()) &&
                compareRecipients(suggestion.recipients);
    }

    private boolean compareRecipients(List<Employee> recipientList) {
        for (Employee employee : recipients) {
            if (recipientList.stream().noneMatch(r -> r.checkIfDataEquals(employee))) {
                return false;
            }
        }
        return true;
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.communication.model;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @ManyToMany
    private List<Employee> participants;

    public Channel(String name, List<Employee> participants) {
        this.name = name;
        this.participants = participants;
    }
}

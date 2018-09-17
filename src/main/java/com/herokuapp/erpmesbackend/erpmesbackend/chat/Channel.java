package com.herokuapp.erpmesbackend.erpmesbackend.chat;

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

    public boolean checkIfDataEquals(Channel channel) {
        return name.equals(channel.getName()) &&
                compareParticipants(channel.getParticipants());
    }

    private boolean compareParticipants(List<Employee> participantList) {
        if (participantList.isEmpty())
            return true;
        for (Employee employee : participants) {
            if (participantList.stream().noneMatch(t -> t.checkIfDataEquals(employee)))
                return false;
        }
        return true;
    }
}

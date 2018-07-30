package com.herokuapp.erpmesbackend.erpmesbackend.teams;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne
    private Employee manager;

    @OneToMany
    private List<Employee> employees;

    public Team(Role role, Employee manager, List<Employee> employees) {
        this.role = role;
        this.manager = manager;
        this.employees = employees;
    }

    public boolean checkIfDataEquals(Team team) {
        return role.equals(team.getRole()) &&
                manager.checkIfDataEquals(team.getManager()) &&
                compareEmployees(team.getEmployees());
    }

    private boolean compareEmployees(List<Employee> employeeList) {
        for (Employee employee : employees) {
            if (employeeList.stream().noneMatch(e -> e.checkIfDataEquals(employee))) {
                return false;
            }
        }
        return true;
    }
}

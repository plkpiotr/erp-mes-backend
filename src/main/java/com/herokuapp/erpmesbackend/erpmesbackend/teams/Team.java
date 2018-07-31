package com.herokuapp.erpmesbackend.erpmesbackend.teams;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
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

    @ManyToOne
    private Employee manager;

    @ManyToMany
    private List<Employee> employees;

    public Team(Role role) {
        this.role = role;
        employees = new ArrayList<>();
    }

    public void addEmployee(Employee employee) {
        if(employee.getRole().name().contains("ADMIN")) {
            if(employee.getRole().equals(Role.ADMIN) && manager == null && role.equals(Role.ADMIN)) {
                manager = employee;
            } else if (!employee.getRole().equals(Role.ADMIN) && role.equals(Role.ADMIN)) {
                employees.add(employee);
            } else if (!employee.getRole().equals(Role.ADMIN) && manager == null) {
                manager = employee;
            }
        } else {
            if (employee.getRole().name().contains(role.name())) {
                employees.add(employee);
            }
        }
    }

    public void removeManager() {
        manager = null;
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
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

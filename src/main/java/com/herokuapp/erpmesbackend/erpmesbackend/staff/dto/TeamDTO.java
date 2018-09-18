package com.herokuapp.erpmesbackend.erpmesbackend.staff.dto;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Team;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class TeamDTO {

    private long id;
    private Role role;
    private EmployeeDTO manager;
    private List<EmployeeDTO> employees;

    public TeamDTO(Team team) {
        this.id = team.getId();
        this.role = team.getRole();
        this.manager = team.getManager() == null ? null : new EmployeeDTO(team.getManager());
        employees = new ArrayList<>();
        if(team.getEmployees().size() > 0) {
            team.getEmployees().forEach(employee -> employees.add(new EmployeeDTO(employee)));
        }
    }

    public boolean checkIfDataEquals(TeamDTO teamDTO) {
        return role.equals(teamDTO.getRole()) &&
                manager.checkIfDataEquals(teamDTO.getManager()) &&
                compareEmployees(teamDTO.getEmployees());
    }

    private boolean compareEmployees(List<EmployeeDTO> employeeList) {
        for (EmployeeDTO employee : employees) {
            if (employeeList.stream().noneMatch(e -> e.checkIfDataEquals(employee))) {
                return false;
            }
        }
        return true;
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.staff.dto;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

    public EmployeeDTO(Employee employee) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.email = employee.getEmail();
        this.role = employee.getRole();
    }

    public boolean isManager() {
        return role.name().contains("ADMIN");
    }
}

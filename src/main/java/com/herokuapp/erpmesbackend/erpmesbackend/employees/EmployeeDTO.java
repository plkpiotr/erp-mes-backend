package com.herokuapp.erpmesbackend.erpmesbackend.employees;

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

    public boolean checkIfDataEquals(EmployeeDTO employeeDTO) {
        return firstName.equals(employeeDTO.getFirstName()) &&
                lastName.equals(employeeDTO.getLastName()) &&
                email.equals(employeeDTO.getEmail()) &&
                role.equals(employeeDTO.getRole());
    }
}

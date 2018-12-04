package com.herokuapp.erpmesbackend.erpmesbackend.staff.request;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @Email
    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private ContractRequest contractRequest;

    public Employee extractUser() {
        Employee employee = new Employee(firstName, lastName, email, Role.ADMIN,
                contractRequest.extractContract());
        employee.changePassword(password);
        return employee;
    }
}

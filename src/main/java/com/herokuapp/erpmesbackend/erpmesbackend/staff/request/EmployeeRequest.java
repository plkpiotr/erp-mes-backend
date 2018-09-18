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
public class EmployeeRequest {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @Email
    @NotNull
    private String email;

    @NotNull
    private Role role;

    @NotNull
    private ContractRequest contractRequest;

    public Employee extractUser() {
        return new Employee(firstName, lastName, email, role,
                contractRequest.extractContract());
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.contracts.Contract;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private Contract contract;
    boolean isPasswordValid;

    public UserDTO(Employee employee) {
        this.id = employee.getId();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.email = employee.getEmail();
        this.role = employee.getRole();
        this.contract = employee.getContract();
        this.isPasswordValid = employee.isPasswordValid();
    }

    public boolean checkIfDataEquals(UserDTO userDTO) {
        return firstName.equals(userDTO.getFirstName()) &&
                lastName.equals(userDTO.getLastName()) &&
                email.equals(userDTO.getEmail()) &&
                role.equals(userDTO.getRole()) &&
                (isPasswordValid == userDTO.isPasswordValid()) &&
                contract.checkIfDataEquals(userDTO.getContract());
    }
}

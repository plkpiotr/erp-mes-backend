package com.herokuapp.erpmesbackend.erpmesbackend.staff.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Getter
@NoArgsConstructor
public class Employee {

    private final int PASSWORD_LENGTH = 8;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String firstName;
    private String lastName;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String password;
    private boolean isPasswordValid;

    @OneToOne
    private Contract contract;

    public Employee(String firstName, String lastName, String email, Role role, Contract contract) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.password = passwordGenerator();
        this.isPasswordValid = false;
        this.contract = contract;
    }

    public boolean isManager() {
        return role.name().contains("ADMIN");
    }

    private String passwordGenerator() {
//        char[] password = new char[PASSWORD_LENGTH];
//        Random r = new Random();
//        for (int i = 0; i < PASSWORD_LENGTH; i++) {
//            password[i] = (char) (r.nextInt('z' - 'a') + 'a');
//        }
//        return new String(password);
        return "haslo123";
    }

    public void encodePassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
        this.isPasswordValid = true;
    }

    public boolean checkIfDataEquals(Employee employee) {
        return firstName.equals(employee.getFirstName()) &&
                lastName.equals(employee.getLastName()) &&
                email.equals(employee.getEmail()) &&
                role.equals(employee.getRole()) &&
                (isPasswordValid == employee.isPasswordValid()) &&
                contract.checkIfDataEquals(employee.getContract());
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.staff.employees;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class EmployeeFactory {

    private final String[] FIRST_NAMES = {"Jolanta", "Jakub", "Mateusz",
            "Wojciech", "Karolina", "Klara", "Justyna", "Maciej",
            "Krzysztof", "Marcin", "Anna", "Katarzyna", "Michał"};
    private final String[] LAST_NAMES = {"Kowalski", "Nowak", "Wójcik",
            "Wojciechowski", "Jędrzejczyk", "Grabowski", "Kopeć"};
    private final Random r;

    public EmployeeFactory() {
        r = new Random();
    }

    private String generate(String[] table) {
        return table[r.nextInt(table.length)];
    }

    private String generateEmail(String firstName, String lastName) {
        return firstName.toLowerCase() + "." + lastName.toLowerCase() + "@domain.com";
    }

    public Role generateRole() {
        return Role.values()[r.nextInt(Role.values().length)];
    }

    private Role generateAdminRole() {
        List<Role> roles = Arrays.asList(Role.values());
        List<Role> adminRoles = roles.stream()
                .filter(role -> role.name().contains("ADMIN"))
                .collect(Collectors.toList());
        return adminRoles.get(r.nextInt(adminRoles.size()));
    }

    private Role generateNonAdminRole() {
        List<Role> roles = Arrays.asList(Role.values());
        List<Role> nonAdminRoles = roles.stream()
                .filter(role -> !role.name().contains("ADMIN"))
                .collect(Collectors.toList());
        return nonAdminRoles.get(r.nextInt(nonAdminRoles.size()));
    }

    public EmployeeRequest generateEmployeeRequest() {
        String firstName = generate(FIRST_NAMES);
        String lastName = generate(LAST_NAMES);
        return new EmployeeRequest(firstName, lastName, generateEmail(firstName, lastName),
                generateRole());
    }

    public EmployeeRequest generateAdminRequest() {
        String firstName = generate(FIRST_NAMES);
        String lastName = generate(LAST_NAMES);
        return new EmployeeRequest(firstName, lastName, generateEmail(firstName, lastName),
                generateAdminRole());
    }

    public EmployeeRequest generateNonAdminRequest() {
        String firstName = generate(FIRST_NAMES);
        String lastName = generate(LAST_NAMES);
        return new EmployeeRequest(firstName, lastName, generateEmail(firstName, lastName),
                generateNonAdminRole());
    }

    public Employee generateEmployee() {
        return generateEmployeeRequest().extractUser();
    }

    public Employee generateAdmin() {
        return generateAdminRequest().extractUser();
    }

    public Employee generateNonAdmin() {
        return generateNonAdminRequest().extractUser();
    }

}

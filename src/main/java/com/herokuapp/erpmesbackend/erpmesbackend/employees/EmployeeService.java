package com.herokuapp.erpmesbackend.erpmesbackend.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.contracts.ContractRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotAManagerException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;

    public void checkIfCanBeAdded(EmployeeRequest request) {
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidRequestException("This username (email) is already taken!");
        }
        if (request.getRole().name().contains("ADMIN") &&
                employeeRepository.findByRole(request.getRole()).isPresent()) {
            throw new InvalidRequestException("There can only be one " + request
                    .getRole().name());
        }
    }

    public void saveEmployee(Employee employee) {
        contractRepository.save(employee.getContract());
        employeeRepository.save(employee);
        addToTeam(employee);
    }

    public void checkIfEmployeeExists(long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such employees doesn't exist!");
        }
    }

    public void removeEmployee(Employee employee) {
        removeFromTeam(employee);
        employeeRepository.delete(employee);
        contractRepository.delete(employee.getContract());
    }

    public void checkIfIsManager(Long id) {
        if (!employeeRepository.findById(id).get().isManager()) {
            throw new NotAManagerException("This employee is not a manager and therefore can't have subordinates!");
        }
    }

    public List<EmployeeDTO> getSubordinates(long id) {
        Optional<Team> team = teamRepository.findByManagerId(id);
        if (!team.isPresent()) {
            return new ArrayList<>();
        }
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        team.get().getEmployees().forEach(employee -> employeeDTOS.add(new EmployeeDTO(employee)));
        return employeeDTOS;
    }

    public void validatePassword(long id, String password) {
        Employee employee = employeeRepository.findById(id).get();
        if (employee.isPasswordValid()) {
            throw new InvalidRequestException("This employee has already validated their password!");
        }
        employee.changePassword(bcryptEncoder.encode(password));
        employeeRepository.save(employee);
    }

    private void removeFromTeam(Employee employee) {
        Role role = employee.isManager() ? mapToNoAdmin(employee.getRole()) : employee.getRole();
        Team team = teamRepository.findByRole(role);
        if (employee.isManager()) {
            if (employee.getRole().equals(Role.ADMIN)) {
                Team adminTeam = teamRepository.findByRole(Role.ADMIN);
                adminTeam.removeManager();
                teamRepository.save(adminTeam);
            } else {
                Team adminTeam = teamRepository.findByRole(Role.ADMIN);
                adminTeam.removeEmployee(employee);
                teamRepository.save(adminTeam);
                team.removeManager();
                teamRepository.save(team);
            }
        } else {
            team.removeEmployee(employee);
            teamRepository.save(team);
        }
    }

    private void addToTeam(Employee employee) {
        Role role = employee.getRole();
        Team team;
        if (role.name().contains("ADMIN")) {
            Team adminTeam = teamRepository.findByRole(Role.ADMIN);
            adminTeam.addEmployee(employee);
            teamRepository.save(adminTeam);
            Role newRole = mapToNoAdmin(role);
            team = teamRepository.findByRole(newRole);
            team.addEmployee(employee);
        } else {
            team = teamRepository.findByRole(role);
            team.addEmployee(employee);
        }
        teamRepository.save(team);
    }

    public Role mapToNoAdmin(Role role) {
        switch (role) {
            case ADMIN_ACCOUNTANT:
                return Role.ACCOUNTANT;
            case ADMIN_ANALYST:
                return Role.ANALYST;
            case ADMIN_WAREHOUSE:
                return Role.WAREHOUSE;
            case ADMIN:
                return Role.ADMIN;
        }
        return role;
    }

    public List<EmployeeDTO> mapToDtos(List<Employee> employees) {
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        employees.forEach(employee -> employeeDTOS.add(new EmployeeDTO(employee)));
        return employeeDTOS;
    }

    public List<EmployeeDTO> filterByPrivilege(List<EmployeeDTO> employeeDTOS, String privilege) {
        if (privilege.equalsIgnoreCase("admin")) {
            return employeeDTOS.stream()
                    .filter(employee -> employee.isManager())
                    .collect(Collectors.toList());
        } else if (privilege.equalsIgnoreCase("user")) {
            return employeeDTOS.stream()
                    .filter(employee -> !employee.isManager())
                    .collect(Collectors.toList());
        }
        return employeeDTOS;
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.contracts.Contract;
import com.herokuapp.erpmesbackend.erpmesbackend.contracts.ContractRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotAManagerException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;

    //sequence for manual testing
    //unit tests fail with this enabled
    @PostConstruct
    public void init() {
        Contract contract = new Contract("123", 26, 50000);
        Employee ceo = new Employee("Szef", "Ceo", "szef.ceo@company.com", Role.ADMIN,
                contract);
        ceo.setPassword(bcryptEncoder.encode(ceo.getPassword()));
        contractRepository.save(contract);
        employeeRepository.save(ceo);
//        EmployeeFactory employeeFactory = new EmployeeFactory();
//        Employee admin = employeeFactory.generateAdmin();
//        admin.setPassword(bcryptEncoder.encode(admin.getPassword()));
//        contractRepository.save(admin.getContract());
//        employeeRepository.save(admin);
//        for (int i = 0; i < 5; i++) {
//            Employee employee = employeeFactory.generateNonAdmin();
//            if (!employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
//                employee.setPassword(bcryptEncoder.encode(employee.getPassword()));
//                contractRepository.save(employee.getContract());
//                employeeRepository.save(employee);
//            }
//        }
//        for (int i = 0; i < 10; i++) {
//            Employee employee = employeeFactory.generateEmployee();
//            if (!employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
//                employee.setPassword(bcryptEncoder.encode(employee.getPassword()));
//                contractRepository.save(employee.getContract());
//                employeeRepository.save(employee);
//            }
//        }
    }

    @GetMapping("/employees")
    @ResponseStatus(HttpStatus.OK)
    public List<Employee> getAllEmployees(@RequestParam(value = "privilege", required = false) String privilege) {
        List<Employee> employees = employeeRepository.findAll();
        if (privilege != null) {
            if (privilege.equalsIgnoreCase("admin")) {
                return employees.stream()
                        .filter(employee -> employee.isManager())
                        .collect(Collectors.toList());
            } else if (privilege.equalsIgnoreCase("user")) {
                return employees.stream()
                        .filter(employee -> !employee.isManager())
                        .collect(Collectors.toList());
            }
        }
        return employees;
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public Employee addNewEmployee(@RequestBody EmployeeRequest request) {
        Employee employee = request.extractUser();
        employee.setPassword(bcryptEncoder.encode(employee.getPassword()));
        checkIfCanBeAdded(request);
        contractRepository.save(employee.getContract());
        employeeRepository.save(employee);
        addToTeam(employee);
        return employee;
    }

    @GetMapping("/employees/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getOneEmployee(@PathVariable("id") long id) {
        checkIfEmployeeExists(id);
        return new UserDTO(employeeRepository.findById(id).get());
    }

    @DeleteMapping("/employees/{id}")
    public HttpStatus fireEmployee(@PathVariable("id") long id) {
        checkIfEmployeeExists(id);
        Employee employee = employeeRepository.findById(id).get();
        removeFromTeam(employee);
        employeeRepository.delete(employee);
        contractRepository.delete(employee.getContract());
        return HttpStatus.OK;
    }

    @GetMapping("/employees/{id}/subordinates")
    public List<EmployeeDTO> getSubordinates(@PathVariable("id") long id) {
        checkIfEmployeeExists(id);
        checkIfIsManager(id);
        Optional<Team> team = teamRepository.findByManagerId(id);
        if (!team.isPresent()) {
            return new ArrayList<>();
        }
        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        team.get().getEmployees().forEach(employee -> employeeDTOS.add(new EmployeeDTO(employee)));
        return employeeDTOS;
    }

    @PostMapping("/employees/{id}/validate-password")
    public HttpStatus validatePassword(@PathVariable("id") long id, @RequestBody String password) {
        checkIfEmployeeExists(id);
        Employee employee = employeeRepository.findById(id).get();
        if (employee.isPasswordValid()) {
            throw new InvalidRequestException("This employee has already validated their password!");
        }
        employee.changePassword(bcryptEncoder.encode(password));
        employeeRepository.save(employee);
        return HttpStatus.NO_CONTENT;
    }

    @GetMapping("/logged-in-user")
    public Employee getLoggedInUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        if (!employeeRepository.findByEmail(username).isPresent()) {
            throw new NotFoundException("There is no such user!");
        }
        return employeeRepository.findByEmail(username).get();
    }

    @GetMapping("/profiles/{id}")
    public EmployeeDTO getProfile(@PathVariable("id") long id) {
        checkIfEmployeeExists(id);
        return new EmployeeDTO(employeeRepository.findById(id).get());
    }

    @GetMapping("profiles/{id}/contract")
    public Contract getContract(@PathVariable("id") long id) {
        checkIfEmployeeExists(id);
        return employeeRepository.findById(id).get().getContract();
    }

    private void checkIfEmployeeExists(long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such employees doesn't exist!");
        }
    }

    private void checkIfIsManager(Long id) {
        if (!employeeRepository.findById(id).get().isManager()) {
            throw new NotAManagerException("This employee is not a manager and therefore can't have subordinates!");
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

    private Role mapToNoAdmin(Role role) {
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

    private void checkIfCanBeAdded(EmployeeRequest request) {
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InvalidRequestException("This username (email) is already taken!");
        }
        if (request.getRole().name().contains("ADMIN") &&
                employeeRepository.findByRole(request.getRole()).isPresent()) {
            throw new InvalidRequestException("There can only be one " + request
                    .getRole().name());
        }
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
}

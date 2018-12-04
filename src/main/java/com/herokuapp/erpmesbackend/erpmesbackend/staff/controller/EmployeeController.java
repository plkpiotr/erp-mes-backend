package com.herokuapp.erpmesbackend.erpmesbackend.staff.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.service.EmailService;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.ForbiddenException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.UserDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Contract;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.ContractRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final ContractRepository contractRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bcryptEncoder;
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository, ContractRepository contractRepository,
                              EmailService emailService, BCryptPasswordEncoder bcryptEncoder,
                              EmployeeService employeeService) {
        this.employeeRepository = employeeRepository;
        this.contractRepository = contractRepository;
        this.emailService = emailService;
        this.bcryptEncoder = bcryptEncoder;
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDTO> getAllEmployees(@RequestParam(value = "privilege", required = false) String privilege) {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDTO> employeeDTOS = employeeService.mapToDtos(employees);
        if (privilege != null) {
            employeeDTOS = employeeService.filterByPrivilege(employeeDTOS, privilege);
        }
        return employeeDTOS;
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDTO addNewEmployee(@RequestBody EmployeeRequest request) {
        Employee employee = request.extractUser();
        emailService.sendMessage(employee.getEmail(), "Your first login password",
                Collections.singletonList("Your automatically generated password is: " +
                        employee.getPassword() +
                        ". You will be prompt to change it after your first login attempt."));
        employee.encodePassword(bcryptEncoder.encode(employee.getPassword()));
        employeeService.checkIfCanBeAdded(request);
        employeeService.saveEmployee(employee);
        return new EmployeeDTO(employee);
    }

    @GetMapping("/employees/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO getOneEmployee(@PathVariable("id") long id) {
        employeeService.checkIfEmployeeExists(id);
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        if (employeeRepository.findByEmail(username).get().getId() != id &&
                employeeRepository.findByEmail(username).get().getRole() != Role.ADMIN) {
            throw new ForbiddenException();
        }
        return new UserDTO(employeeRepository.findById(id).get());
    }

    @DeleteMapping("/employees/{id}")
    public HttpStatus fireEmployee(@PathVariable("id") long id) {
        employeeService.checkIfEmployeeExists(id);
        Employee employee = employeeRepository.findById(id).get();
        employeeService.removeEmployee(employee);
        return HttpStatus.OK;
    }

    @GetMapping("/employees/{id}/subordinates")
    public List<EmployeeDTO> getSubordinates(@PathVariable("id") long id) {
        employeeService.checkIfEmployeeExists(id);
        employeeService.checkIfIsManager(id);
        return employeeService.getSubordinates(id);
    }

    @GetMapping("/employees/colleagues")
    public List<EmployeeDTO> getColleagues() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDTO> employeeDTOS = employeeService.mapToDtos(employees);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Employee author = employeeRepository.findByEmail(username).get();

        return employeeDTOS.stream()
                .filter(employeeDTO -> !employeeDTO.getEmail().equals(author.getEmail()))
                .sorted(Comparator.comparing(EmployeeDTO::getRole))
                .collect(Collectors.toList());
    }

    @PostMapping("/employees/{id}/validate-password")
    public HttpStatus validatePassword(@PathVariable("id") long id, @RequestBody String password) {
        employeeService.checkIfEmployeeExists(id);
        employeeService.validatePassword(id, password);
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
        employeeService.checkIfEmployeeExists(id);
        return new EmployeeDTO(employeeRepository.findById(id).get());
    }

    @GetMapping("profiles/{id}/contract")
    public Contract getContract(@PathVariable("id") long id) {
        employeeService.checkIfEmployeeExists(id);
        return employeeRepository.findById(id).get().getContract();
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.contracts.Contract;
import com.herokuapp.erpmesbackend.erpmesbackend.contracts.ContractRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.emails.InboxService;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.ForbiddenException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private BCryptPasswordEncoder bcryptEncoder;

    @Autowired
    private EmployeeService employeeService;

    @PostConstruct
    public void init() {
        Contract contract = new Contract("123", 26, 50000);
        Employee ceo = new Employee("Szef", "Ceo", "szef.ceo@company.com", Role.ADMIN,
                contract);
        ceo.changePassword(bcryptEncoder.encode("haslo123"));
        contractRepository.save(contract);
        employeeRepository.save(ceo);
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
        InboxService inboxService = new InboxService();
        inboxService.readMail();
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

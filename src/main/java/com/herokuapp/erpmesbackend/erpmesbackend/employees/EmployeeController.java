package com.herokuapp.erpmesbackend.erpmesbackend.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotAManagerException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.teams.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
//@CrossOrigin("http://localhost:4200")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TeamRepository teamRepository;

    //sequence for manual testing
    //unit tests fail with this enabled
//    @PostConstruct
//    public void init() {
//        EmployeeFactory employeeFactory = new EmployeeFactory();
//        employeeRepository.save(employeeFactory.generateAdmin());
//        for (int i = 0; i < 5; i++) {
//            employeeRepository.save(employeeFactory.generateNonAdmin());
//        }
//        for (int i = 0; i < 10; i++) {
//            employeeRepository.save(employeeFactory.generateEmployee());
//        }
//    }

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
        employeeRepository.save(employee);
        return employee;
    }

    @GetMapping("/employees/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Employee getOneEmployee(@PathVariable("id") long id) {
        checkIfEmployeeExists(id);
        return employeeRepository.findById(id).get();
    }

    @DeleteMapping("/employees/{id}")
    public HttpStatus fireEmployee(@PathVariable("id") long id) {
        checkIfEmployeeExists(id);
        employeeRepository.delete(employeeRepository.findById(id).get());
        return HttpStatus.OK;
    }

    @GetMapping("/employees/{id}/subordinates")
    public List<Employee> getSubordinates(@PathVariable("id") long id) {
        checkIfEmployeeExists(id);
        checkIfIsManager(id);
        Optional<Team> team = teamRepository.findByManagerId(id);
        if (!team.isPresent()) {
            throw new NotFoundException("This manager doesn't have a team!");
        }
        return team.get().getEmployees();
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
}

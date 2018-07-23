package com.herokuapp.erpmesbackend.erpmesbackend.staff.employees;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
public class EmployeeController {

    @Autowired
    private EmployeeRepository employeeRepository;

    //sequence for manual testing
    //unit tests fail with this enabled
    /*@PostConstruct
    public void init() {
        EmployeeFactory employeeFactory = new EmployeeFactory();
        employeeRepository.save(employeeFactory.generateAdmin());
        for(int i = 0; i < 5; i++) {
            employeeRepository.save(employeeFactory.generateNonAdmin());
        }
        for(int i = 0; i < 10; i++) {
            employeeRepository.save(employeeFactory.generateEmployee());
        }
    }*/

    @GetMapping("/employees")
    @ResponseStatus(HttpStatus.OK)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
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

    private void checkIfEmployeeExists(long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such employees doesn't exist!");
        }
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.security;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service("userService")
public class UserService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public UserService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public UserDetails loadUserByUsername(String username) throws NotFoundException {
        if(!employeeRepository.findByEmail(username).isPresent()){
            throw new NotFoundException("Invalid username or password.");
        }
        Employee employee  = employeeRepository.findByEmail(username).get();
        return new User(employee.getEmail(), employee.getPassword(), getAuthority(employee));
    }

    private List<SimpleGrantedAuthority> getAuthority(Employee employee) {
        return Arrays.asList(new SimpleGrantedAuthority(employee.getRole().name()));
    }
}

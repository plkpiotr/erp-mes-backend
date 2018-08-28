package com.herokuapp.erpmesbackend.erpmesbackend.security;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
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

    @Autowired
    private EmployeeRepository employeeRepository;

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

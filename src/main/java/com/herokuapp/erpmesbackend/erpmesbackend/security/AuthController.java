package com.herokuapp.erpmesbackend.erpmesbackend.security;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/generate-token")
    public JwtToken login(@RequestBody Credentials credentials) throws AuthenticationException {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getEmail(),
                        credentials.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        checkIfEmployeeExists(credentials.getEmail());
        Employee employee = employeeRepository.findByEmail(credentials.getEmail()).get();
        String token = jwtTokenUtil.generateToken(employee);
        return new JwtToken(token);
    }

    private void checkIfEmployeeExists(String email) {
        if (!employeeRepository.findByEmail(email).isPresent()) {
            throw new NotFoundException("Such employees doesn't exist!");
        }
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.teams;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotAManagerException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @PostConstruct
    public void setupTeams() {
        teamRepository.save(new Team(Role.ADMIN));
        teamRepository.save(new Team(Role.ACCOUNTANT));
        teamRepository.save(new Team(Role.ANALYST));
        teamRepository.save(new Team(Role.WAREHOUSE));
    }

    @GetMapping("/teams")
    @ResponseStatus(HttpStatus.OK)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @GetMapping("/teams/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Team getOneTeam(@PathVariable("id") long id) {
        checkIfTeamExists(id);
        return teamRepository.findById(id).get();
    }

    private void checkIfTeamExists(Long id) {
        if(!teamRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such team doesn't exist!");
        }
    }
}

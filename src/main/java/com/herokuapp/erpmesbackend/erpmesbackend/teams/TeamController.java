package com.herokuapp.erpmesbackend.erpmesbackend.teams;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostConstruct
    public void setupTeams() {
        Team team = new Team(Role.ADMIN);
        team.addEmployee(employeeRepository.findById(1L).get());
        teamRepository.save(team);
        teamRepository.save(new Team(Role.ACCOUNTANT));
        teamRepository.save(new Team(Role.ANALYST));
        teamRepository.save(new Team(Role.WAREHOUSE));
    }

    @GetMapping("/teams")
    @ResponseStatus(HttpStatus.OK)
    public List<TeamDTO> getAllTeams() {
        List<TeamDTO> teamDTOS = new ArrayList<>();
        teamRepository.findAll().forEach(team -> teamDTOS.add(new TeamDTO(team)));
        return teamDTOS;
    }

    @GetMapping("/teams/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TeamDTO getOneTeam(@PathVariable("id") long id) {
        checkIfTeamExists(id);
        return new TeamDTO(teamRepository.findById(id).get());
    }

    private void checkIfTeamExists(Long id) {
        if (!teamRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such team doesn't exist!");
        }
    }
}

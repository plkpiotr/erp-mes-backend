package com.herokuapp.erpmesbackend.erpmesbackend.staff.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.TeamDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TeamController {

    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public TeamController(TeamRepository teamRepository, EmployeeRepository employeeRepository) {
        this.teamRepository = teamRepository;
        this.employeeRepository = employeeRepository;
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

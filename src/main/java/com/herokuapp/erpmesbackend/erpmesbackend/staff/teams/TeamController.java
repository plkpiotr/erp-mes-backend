package com.herokuapp.erpmesbackend.erpmesbackend.staff.teams;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotAManagerException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
public class TeamController {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/teams")
    @ResponseStatus(HttpStatus.OK)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @PostMapping("/teams")
    @ResponseStatus(HttpStatus.CREATED)
    public Team addNewTeam(@RequestBody @Valid TeamRequest request) {
        checkTeamConditions(request);

        Employee manager = employeeRepository.findById(request.getManagerId()).get();
        List<Employee> employees = new ArrayList<>();
        request.getEmployeeIds().forEach(id -> employees.add(employeeRepository.findById(id).get()));

        Team team = new Team(request.getRole(), manager, employees);
        teamRepository.save(team);
        return team;
    }

    @GetMapping("/teams/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Team getOneTeam(@PathVariable("id") long id) {
        checkIfTeamExists(id);
        return teamRepository.findById(id).get();
    }

    @DeleteMapping("/teams/{id}")
    public HttpStatus removeTeam(@PathVariable("id") long id) {
        checkIfTeamExists(id);
        teamRepository.delete(teamRepository.findById(id).get());
        return HttpStatus.OK;
    }

    private void checkIfTeamExists(Long id) {
        if(!teamRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such team doesn't exist!");
        }
    }

    private void checkTeamConditions(TeamRequest request) {
        if (hasManagerTeam(request.getManagerId())) {
            throw new InvalidRequestException("This manager already has a team to manage!");
        }
        request.getEmployeeIds().forEach(id -> {
            checkIfEmployeeExists(id);
            if (hasEmployeeTeam(id)) {
                throw new InvalidRequestException("This employee already belongs to a team!");
            }
        });
    }

    private boolean hasManagerTeam(Long id) {
        checkIfEmployeeExists(id);
        checkIfIsManager(id);
        return teamRepository.findByManagerId(id).isPresent();
    }

    private boolean hasEmployeeTeam(Long id) {
        Employee employee = employeeRepository.findById(id).get();
        List<Team> allTeams = teamRepository.findAll();
        return allTeams.stream().anyMatch(team -> team.getEmployees().contains(employee));
    }

    private void checkIfEmployeeExists(long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such employees doesn't exist!");
        }
    }

    private void checkIfIsManager(Long id) {
        if (!employeeRepository.findById(id).get().isManager()) {
            throw new NotAManagerException("This employee is not a manager!");
        }
    }
}

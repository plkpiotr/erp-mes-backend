package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.teams;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.teams.Team;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.teams.TeamRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteTeamTest extends FillBaseTemplate {

    private List<Team> teams;
    private Team deletedTeam;

    @Before
    public void init() {
        addOneAdminRequest(true);
        addNonAdminRequests(true);
        teams = addTeamRequests(true);
        deletedTeam = restTemplate.getForEntity("/teams/{id}", Team.class, 1).getBody();
    }

    @Test
    public void checkIfResponseDoesNotContainDeletedTeam() {
        restTemplate.delete("/teams/{id}", 1);
        List<Team> fetchedTeams = Arrays.asList(restTemplate
                .getForEntity("/teams", Team[].class).getBody());

        assertFalse(fetchedTeams.stream().anyMatch(team -> team.checkIfDataEquals(deletedTeam)));
    }
}

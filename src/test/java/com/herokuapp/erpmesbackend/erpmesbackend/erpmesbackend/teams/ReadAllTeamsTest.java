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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadAllTeamsTest extends FillBaseTemplate {

    private List<Team> teams;

    @Before
    public void init() {
        addOneAdminRequest(true);
        addNonAdminRequests(true);
        teams = addTeamRequests(true);
    }

    @Test
    public void checkIfResponseContainsAllTeams() {
        ResponseEntity<Team[]> forEntity = restTemplate.getForEntity("/teams", Team[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Team> fetchedTeams = Arrays.asList(forEntity.getBody());
        for (Team team : fetchedTeams) {
            assertTrue(teams.stream().anyMatch(t -> t.checkIfDataEquals(team)));
        }
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.errors;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EntitiesConflictTest extends FillBaseTemplate {

    @Before
    public void init() {
        addOneAdminRequest(true);
        addOneAdminRequest(true);
        addNonAdminRequests(true);
        addOneTeamRequest(true, teamRequest);
        addOneTeamRequest(true, teamRequest);
        addOneHolidayRequest(3, true);
    }

    @Test
    public void assertThatResponseStatus409TeamConflict() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                1, String.class, 1, 4
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(responseEntity.getBody()).contains("team");
    }

    @Test
    public void assertThatResponseStatus409HolidayConflict() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                "/employees/{managerId}/subordinates/{subordinateId}/holidays?approve=true",
                1, String.class, 1, 12
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(responseEntity.getBody()).contains("holiday");
    }
}

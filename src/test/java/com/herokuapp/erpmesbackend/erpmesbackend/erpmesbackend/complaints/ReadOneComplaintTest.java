package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.complaints;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints.Complaint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadOneComplaintTest extends FillBaseTemplate {

    private List<Complaint> complaints;

    @Before
    public void init() {
        setupToken();
        addManyItemRequests(true);
        addManyComplaintRequests(true);
        complaints = Arrays.asList(restTemplate.exchange("/complaints", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Complaint[].class).getBody());
    }

    @Test
    public void checkIfResponseContainsRequestedComplaint() {
        for (int i = 0; i < complaintRequests.size(); i++) {
            ResponseEntity<Complaint> forEntity = restTemplate.exchange("/complaints/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Complaint.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertTrue(complaints.stream().anyMatch(c -> c.checkIfDataEquals(forEntity.getBody())));
        }
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.complaints;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Complaint;
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
public class AddOneComplaintTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addOneComplaintRequest(false);
    }

    @Test
    public void checkIfResponseContainsAddedComplaint() {
        ResponseEntity<Complaint> complaintResponseEntity = restTemplate.postForEntity("/complaints",
                new HttpEntity<>(complaintRequest, requestHeaders), Complaint.class);
        assertThat(complaintResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Complaint complaint = complaintResponseEntity.getBody();
        List<Complaint> complaints = Arrays.asList(restTemplate.exchange("/complaints", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Complaint[].class).getBody());
        assertTrue(complaints.stream().anyMatch(c -> c.checkIfDataEquals(complaint)));
    }
}

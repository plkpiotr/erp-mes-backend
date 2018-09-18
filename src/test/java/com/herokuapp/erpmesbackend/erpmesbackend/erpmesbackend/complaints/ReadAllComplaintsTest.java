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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadAllComplaintsTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addManyItemRequests(true);
        addManyComplaintRequests(true);
    }

    @Test
    public void checkIfResponseContainsAllComplaints() {
        ResponseEntity<Complaint[]> forEntity = restTemplate.exchange("/complaints", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Complaint[].class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Complaint> complaints = Arrays.asList(forEntity.getBody());
        assertThat(complaints.size()).isGreaterThanOrEqualTo(3);
    }

}

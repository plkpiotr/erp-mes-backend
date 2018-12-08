package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Complaint;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.ComplaintStatus;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Resolution;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.ShopServiceRequest;
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
public class ComplaintControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupItems();
        setupComplaints();
    }

    @Test
    public void readAllComplaintsTest() {
        ResponseEntity<Complaint[]> forEntity = restTemplate.exchange("/complaints", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Complaint[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Complaint> complaints = Arrays.asList(forEntity.getBody());
        complaintRequests.forEach(request -> complaints.stream()
                .anyMatch(complaint -> checkIfComplaintAndRequestMatch(complaint, request))
        );
    }

    @Test
    public void readOneComplaintTest() {
        for (int i = 0; i < complaintRequests.size(); i++) {
            ResponseEntity<Complaint> forEntity = restTemplate.exchange("/complaints/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Complaint.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(forEntity.getBody()).isNotNull();
        }
    }

    @Test
    public void addNewComplaintTest() {
        ShopServiceRequest oneComplaintRequestWithItemId = getOneComplaintRequestWithItemId(1);
        ResponseEntity<Complaint> complaintResponseEntity = restTemplate.postForEntity("/complaints",
                new HttpEntity<>(oneComplaintRequestWithItemId, requestHeaders), Complaint.class);
        assertThat(complaintResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertTrue(checkIfComplaintAndRequestMatch(complaintResponseEntity.getBody(), oneComplaintRequestWithItemId));
    }

    @Test
    public void changeComplaintResolutionTest() {
        ResponseEntity<Complaint> exchange = restTemplate.exchange("/complaints/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Complaint.class, 1);
        assertThat(exchange.getBody().getResolution()).isEqualTo(Resolution.UNRESOLVED);

        ResponseEntity<Complaint> updateStatusResponse = restTemplate.exchange("/complaints/{id}/resolution",
                HttpMethod.PUT, new HttpEntity<>(Resolution.MONEY_RETURN.name(), requestHeaders),
                Complaint.class, 1);
        assertThat(updateStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateStatusResponse.getBody().getResolution()).isEqualTo(Resolution.MONEY_RETURN);
    }

    @Test
    public void changeComplaintStatusTest() {
        ResponseEntity<Complaint> exchange = restTemplate.exchange("/complaints/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Complaint.class, 1);
        assertThat(exchange.getBody().getStatus()).isEqualTo(ComplaintStatus.IN_PROGRESS);

        ResponseEntity<Complaint> updateStatusResponse = restTemplate.exchange("/complaints/{id}",
                HttpMethod.PUT, new HttpEntity<>(ComplaintStatus.DECLINED.name(), requestHeaders),
                Complaint.class, 1);
        assertThat(updateStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateStatusResponse.getBody().getStatus()).isEqualTo(ComplaintStatus.DECLINED);
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> forEntity = restTemplate.exchange("/complaints/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private boolean checkIfComplaintAndRequestMatch(Complaint complaint, ShopServiceRequest request) {
        return complaint.getFirstName().equals(request.getFirstName()) &&
                complaint.getLastName().equals(request.getLastName()) &&
                complaint.getEmail().equals(request.getEmail()) &&
                complaint.getPhoneNumber().equals(request.getPhoneNumber()) &&
                complaint.getStreet().equals(request.getStreet()) &&
                complaint.getHouseNumber().equals(request.getHouseNumber()) &&
                complaint.getCity().equals(request.getCity()) &&
                complaint.getPostalCode().equals(request.getPostalCode()) &&
                complaint.getScheduledFor().equals(request.getScheduledFor()) &&
                complaint.getDeliveryItems().size() == request.getDeliveryItemRequests().size() &&
                complaint.getRequestedResolution().equals(request.getRequestedResolution()) &&
                complaint.getFault().equals(request.getFault());
    }
}

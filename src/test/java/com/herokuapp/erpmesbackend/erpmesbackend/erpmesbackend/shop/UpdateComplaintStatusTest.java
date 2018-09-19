package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.shop;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Complaint;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.ComplaintStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateComplaintStatusTest extends FillBaseTemplate {

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void checkIfComplaintStatusHasChanged() {
        ResponseEntity<Complaint> exchange = restTemplate.exchange("/complaints/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Complaint.class, 1);
        assertThat(exchange.getBody().getStatus()).isEqualTo(ComplaintStatus.IN_PROGRESS);

        ResponseEntity<Complaint> updateStatusResponse = restTemplate.exchange("/complaints/{id}",
                HttpMethod.PUT, new HttpEntity<>(ComplaintStatus.DECLINED.name(), requestHeaders),
                Complaint.class, 1);
        assertThat(updateStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateStatusResponse.getBody().getStatus()).isEqualTo(ComplaintStatus.DECLINED);
    }
}

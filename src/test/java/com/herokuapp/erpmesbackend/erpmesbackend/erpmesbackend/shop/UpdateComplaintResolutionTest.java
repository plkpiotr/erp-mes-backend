package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.shop;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Complaint;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Resolution;
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
public class UpdateComplaintResolutionTest extends FillBaseTemplate {

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void checkIfComplaintResolutionHasChanged() {
        ResponseEntity<Complaint> exchange = restTemplate.exchange("/complaints/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Complaint.class, 1);
        assertThat(exchange.getBody().getResolution()).isEqualTo(Resolution.UNRESOLVED);

        ResponseEntity<Complaint> updateStatusResponse = restTemplate.exchange("/complaints/{id}/resolution",
                HttpMethod.PUT, new HttpEntity<>(Resolution.MONEY_RETURN.name(), requestHeaders),
                Complaint.class, 1);
        assertThat(updateStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateStatusResponse.getBody().getResolution()).isEqualTo(Resolution.MONEY_RETURN);
    }
}

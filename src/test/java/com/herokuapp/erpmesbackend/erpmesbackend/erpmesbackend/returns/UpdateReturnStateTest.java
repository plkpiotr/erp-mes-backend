package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.returns;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Return;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.ReturnStatus;
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
public class UpdateReturnStateTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addManyItemRequests(true);
        addOneReturnRequest(true);
    }

    @Test
    public void checkIfReturnStatusHasChanged() {
        ResponseEntity<Return> exchange = restTemplate.exchange("/returns/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Return.class, 1);
        assertThat(exchange.getBody().getStatus()).isEqualTo(ReturnStatus.IN_PROGRESS);

        ResponseEntity<Return> updateStatusResponse = restTemplate.exchange("/returns/{id}",
                HttpMethod.PUT, new HttpEntity<>(ReturnStatus.ACCEPTED.name(), requestHeaders), Return.class,
                1);
        assertThat(updateStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateStatusResponse.getBody().getStatus()).isEqualTo(ReturnStatus.ACCEPTED);
    }
}

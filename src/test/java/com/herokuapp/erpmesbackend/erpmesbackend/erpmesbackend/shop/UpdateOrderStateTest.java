package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.shop;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Status;
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
public class UpdateOrderStateTest extends FillBaseTemplate {

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void checkIfOrderStatusHasChanged() {
        ResponseEntity<Order> exchange = restTemplate.exchange("/orders/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Order.class, 1);
        assertThat(exchange.getBody().getStatus()).isEqualTo(Status.WAITING_FOR_PAYMENT);

        ResponseEntity<Order> updateStatusResponse = restTemplate.exchange("/orders/{id}",
                HttpMethod.PUT, new HttpEntity<>(Status.IN_PROGRESS.name(), requestHeaders), Order.class,
                1);
        assertThat(updateStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateStatusResponse.getBody().getStatus()).isEqualTo(Status.IN_PROGRESS);
    }
}

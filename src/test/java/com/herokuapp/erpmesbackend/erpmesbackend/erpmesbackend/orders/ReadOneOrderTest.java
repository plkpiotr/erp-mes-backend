package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.orders;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.Order;
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
public class ReadOneOrderTest extends FillBaseTemplate {

    private List<Order> orders;

    @Before
    public void init() {
        setupToken();
        addManyItemRequests(true);
        addManyOrderRequests(true);
        orders = Arrays.asList(restTemplate.exchange("/orders", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Order[].class).getBody());
    }

    @Test
    public void checkIfResponseContainsRequestedOrder() {
        for (int i = 0; i < orderRequests.size(); i++) {
            ResponseEntity<Order> forEntity = restTemplate.exchange("/orders/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Order.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertTrue(orders.stream().anyMatch(o -> o.checkIfDataEquals(forEntity.getBody())));
        }
    }
}

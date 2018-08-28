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
public class AddOneOrderTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addOneOrderRequest(false);
    }

    @Test
    public void checkIfResponseContainsAddedOrder() {
        ResponseEntity<Order> orderResponseEntity = restTemplate.postForEntity("/orders",
                new HttpEntity<>(orderRequest, requestHeaders), Order.class);
        assertThat(orderResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Order order = orderResponseEntity.getBody();
        List<Order> orders = Arrays.asList(restTemplate.exchange("/orders", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Order[].class).getBody());
        assertTrue(orders.stream().anyMatch(o -> o.checkIfDataEquals(order)));
    }
}

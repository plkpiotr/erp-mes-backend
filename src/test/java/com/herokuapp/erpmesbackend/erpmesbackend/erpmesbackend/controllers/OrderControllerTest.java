package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Status;
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
public class OrderControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupItemsAndSupply();
        setupOrders();
    }

    @Test
    public void readAllOrdersTest() {
        ResponseEntity<Order[]> forEntity = restTemplate.exchange("/orders", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Order[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Order> orders = Arrays.asList(forEntity.getBody());
        orderRequests.forEach(request -> orders.stream()
            .anyMatch(order -> checkIfOrderAndRequestMatch(order, request))
        );
    }

    @Test
    public void readOneOrderTest() {
        for (int i = 0; i < orderRequests.size(); i++) {
            ResponseEntity<Order> forEntity = restTemplate.exchange("/orders/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Order.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(forEntity.getBody()).isNotNull();
        }
    }

    @Test
    public void addNewOrderTest() {
        ShopServiceRequest oneShopServiceRequestWithItemId = getOneShopServiceRequestWithItemId(1);
        ResponseEntity<Order> orderResponseEntity = restTemplate.postForEntity("/orders",
                new HttpEntity<>(oneShopServiceRequestWithItemId, requestHeaders), Order.class);
        assertThat(orderResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertTrue(checkIfOrderAndRequestMatch(orderResponseEntity.getBody(), oneShopServiceRequestWithItemId));
    }

    @Test
    public void changeOrderStatusTest() {
        ResponseEntity<Order> exchange = restTemplate.exchange("/orders/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Order.class, 1);
        assertThat(exchange.getBody().getStatus()).isEqualTo(Status.WAITING_FOR_PAYMENT);

        ResponseEntity<Order> updateStatusResponse = restTemplate.exchange("/orders/{id}",
                HttpMethod.PUT, new HttpEntity<>(Status.IN_PROGRESS.name(), requestHeaders), Order.class,
                1);
        assertThat(updateStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateStatusResponse.getBody().getStatus()).isEqualTo(Status.IN_PROGRESS);
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> forEntity = restTemplate.exchange("/orders/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private boolean checkIfOrderAndRequestMatch(Order order, ShopServiceRequest request) {
        return order.getFirstName().equals(request.getFirstName()) &&
                order.getLastName().equals(request.getLastName()) &&
                order.getEmail().equals(request.getEmail()) &&
                order.getPhoneNumber().equals(request.getPhoneNumber()) &&
                order.getStreet().equals(request.getStreet()) &&
                order.getHouseNumber().equals(request.getHouseNumber()) &&
                order.getCity().equals(request.getCity()) &&
                order.getPostalCode().equals(request.getPostalCode()) &&
                order.getScheduledFor().equals(request.getScheduledFor()) &&
                order.getDeliveryItems().size() == request.getDeliveryItemRequests().size();
    }
}

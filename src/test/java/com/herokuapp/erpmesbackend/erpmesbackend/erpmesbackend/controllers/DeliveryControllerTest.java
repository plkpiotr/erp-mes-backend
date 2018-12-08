package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Delivery;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.DeliveryItemRequest;
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
public class DeliveryControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupItems();
        setupDeliveries();
    }

    @Test
    public void readAllDeliveriesTest() {
        ResponseEntity<Delivery[]> forEntity = restTemplate.exchange("/deliveries", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Delivery[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(forEntity.getBody().length).isGreaterThan(0);
    }

    @Test
    public void addNewDeliveryTest() {
        ResponseEntity<Delivery> deliveryResponseEntity = restTemplate.postForEntity("/deliveries",
                new HttpEntity<>(getOneDeliveryRequest(), requestHeaders), Delivery.class);
        assertThat(deliveryResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void readOneDeliveryTest() {
        for (int i = 0; i < deliveryRequests.size(); i++) {
            ResponseEntity<Delivery> forEntity = restTemplate.exchange("/deliveries/{id}",
                    HttpMethod.GET, new HttpEntity<>(null, requestHeaders),
                    Delivery.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(forEntity.getBody()).isNotNull();
        }
    }

    @Test
    public void recommendDeliveryTest() {
        setupItemsAndSupply();
        setupOrders();

        ResponseEntity<DeliveryItemRequest[]> exchange = restTemplate.exchange("/deliveries/recommended-delivery",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders), DeliveryItemRequest[].class);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getBody().length).isGreaterThan(0);
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> forEntity = restTemplate.exchange("/deliveries/{id}",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders),
                String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

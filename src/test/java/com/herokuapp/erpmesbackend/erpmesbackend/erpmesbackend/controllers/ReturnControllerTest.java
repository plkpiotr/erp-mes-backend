package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Return;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.ReturnStatus;
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
public class ReturnControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupItemsAndSupply();
        setupReturns();
    }

    @Test
    public void readAllReturnsTest() {
        ResponseEntity<Return[]> forEntity = restTemplate.exchange("/returns", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Return[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Return> returns = Arrays.asList(forEntity.getBody());
        returnRequests.forEach(request -> returns.stream()
                .anyMatch(ret -> checkIfReturnAndRequestMatch(ret, request))
        );
    }

    @Test
    public void readOneReturnTest() {
        for (int i = 0; i < returnRequests.size(); i++) {
            ResponseEntity<Return> forEntity = restTemplate.exchange("/returns/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Return.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(forEntity.getBody()).isNotNull();
        }
    }

    @Test
    public void addNewReturnTest() {
        ShopServiceRequest oneShopServiceRequestWithItemId = getOneShopServiceRequestWithItemId(1);
        ResponseEntity<Return> returnResponseEntity = restTemplate.postForEntity("/returns",
                new HttpEntity<>(oneShopServiceRequestWithItemId, requestHeaders), Return.class);
        assertThat(returnResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertTrue(checkIfReturnAndRequestMatch(returnResponseEntity.getBody(), oneShopServiceRequestWithItemId));
    }

    @Test
    public void changeReturnStatusTest() {
        ResponseEntity<Return> exchange = restTemplate.exchange("/returns/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Return.class, 1);
        assertThat(exchange.getBody().getStatus()).isEqualTo(ReturnStatus.IN_PROGRESS);

        ResponseEntity<Return> updateStatusResponse = restTemplate.exchange("/returns/{id}",
                HttpMethod.PUT, new HttpEntity<>(ReturnStatus.ACCEPTED.name(), requestHeaders), Return.class,
                1);
        assertThat(updateStatusResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateStatusResponse.getBody().getStatus()).isEqualTo(ReturnStatus.ACCEPTED);
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> forEntity = restTemplate.exchange("/returns/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private boolean checkIfReturnAndRequestMatch(Return ret, ShopServiceRequest request) {
        return ret.getFirstName().equals(request.getFirstName()) &&
                ret.getLastName().equals(request.getLastName()) &&
                ret.getEmail().equals(request.getEmail()) &&
                ret.getPhoneNumber().equals(request.getPhoneNumber()) &&
                ret.getStreet().equals(request.getStreet()) &&
                ret.getHouseNumber().equals(request.getHouseNumber()) &&
                ret.getCity().equals(request.getCity()) &&
                ret.getPostalCode().equals(request.getPostalCode()) &&
                ret.getScheduledFor().equals(request.getScheduledFor()) &&
                ret.getDeliveryItems().size() == request.getDeliveryItemRequests().size();
    }
}

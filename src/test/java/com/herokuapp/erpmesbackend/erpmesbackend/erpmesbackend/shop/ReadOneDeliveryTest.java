package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.shop;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Delivery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadOneDeliveryTest extends FillBaseTemplate {

    private List<Delivery> deliveries;

    @Before
    public void init() {
        super.init();
        deliveries = Arrays.asList(restTemplate.exchange("/deliveries", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Delivery[].class).getBody());
    }

    @Test
    public void checkIfResponseContainsRequestedDelivery() {
        for(int i = 0; i < deliveryRequests.size(); i++) {
            ResponseEntity<Delivery> forEntity = restTemplate.exchange("/deliveries/{id}",
                    HttpMethod.GET, new HttpEntity<>(null, requestHeaders),
                    Delivery.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertTrue(deliveries.stream().anyMatch(delivery -> delivery
                    .checkIfDataEquals(forEntity.getBody())));
        }
    }
}

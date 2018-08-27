package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.items;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.Item;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadOneItemTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addManyItemRequests(true);
    }

    @Test
    public void assertResponseContainsRequestedItem() {
        for(int i = 0; i < itemRequests.size(); i++) {
            ResponseEntity<Item> forEntity = restTemplate.exchange("/items/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Item.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertTrue(itemRequests.stream().anyMatch(request -> request.extractItem()
                    .checkIfDataEquals(forEntity.getBody())));
        }
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.items;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddNewItemTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addOneItemRequest(false);
    }

    @Test
    public void checkIfResponseContainsAddedItem() {
        ResponseEntity<Item> itemResponseEntity = restTemplate.postForEntity("/items",
                new HttpEntity<>(itemRequest, requestHeaders), Item.class);
        assertThat(itemResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Item body = itemResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(itemRequest.extractItem()));
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Item;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.ItemRequest;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ItemControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupItems();
    }

    @Test
    public void readAllItemsTest() {
        ResponseEntity<Item[]> forEntity = restTemplate.exchange("/items", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Item[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Item> items = Arrays.asList(forEntity.getBody());
        itemRequests.forEach(request -> items.stream()
                .anyMatch(item -> checkIfItemAndRequestMatch(item, request)));
    }

    @Test
    public void addNewItemTest() {
        ItemRequest addedItem = new ItemRequest("Added item", 100, 150);
        ResponseEntity<Item> itemResponseEntity = restTemplate.postForEntity("/items",
                new HttpEntity<>(addedItem, requestHeaders), Item.class);
        assertThat(itemResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertTrue(checkIfItemAndRequestMatch(itemResponseEntity.getBody(), addedItem));
    }

    @Test
    public void supplyItemTest() {
        Item item = restTemplate.postForEntity("/items", new HttpEntity<>(new ItemRequest("Supplied item",
                        100, 150), requestHeaders), Item.class).getBody();
        int oldQuantity = item.getQuantity();
        ResponseEntity<Item> itemResponseEntity = restTemplate.postForEntity("/items/{id}/supply",
                new HttpEntity<>(10, requestHeaders), Item.class, item.getId());

        assertThat(itemResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(itemResponseEntity.getBody().getQuantity() - oldQuantity).isEqualTo(10);
    }

    @Test
    public void buyItemTest() {
        Item item = restTemplate.postForEntity("/items/{id}/supply",
                new HttpEntity<>(10, requestHeaders), Item.class, 2).getBody();
        int oldQuantity = item.getQuantity();
        ResponseEntity<Item> itemResponseEntity = restTemplate.postForEntity("/items/{id}/buy",
                new HttpEntity<>(5, requestHeaders), Item.class, 2);

        assertThat(itemResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(oldQuantity - itemResponseEntity.getBody().getQuantity()).isEqualTo(5);
    }

    @Test
    public void readOneItemTest() {
        for (int i = 0; i <= itemRequests.size(); i++) {
            ResponseEntity<Item> forEntity = restTemplate.exchange("/items/{id}",
                    HttpMethod.GET, new HttpEntity<>(null, requestHeaders),
                    Item.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(forEntity.getBody()).isNotNull();
        }
    }

    @Test
    public void addSpecialOfferTest() {
        ResponseEntity<Item[]> responseEntity = restTemplate.postForEntity("/set-special-offer?percentOff=20",
                new HttpEntity<>(null, requestHeaders), Item[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Item> items = Arrays.asList(responseEntity.getBody());
        items.forEach(item -> assertFalse(item.getCurrentPrice() == item.getOriginalPrice()));
    }

    @Test
    public void cancelSpecialOfferTest() {
        restTemplate.postForEntity("/set-special-offer?percentOff=20", new HttpEntity<>(null,
                requestHeaders), Item[].class);
        ResponseEntity<Item[]> responseEntity = restTemplate.postForEntity("/cancel-special-offer",
                new HttpEntity<>(null, requestHeaders), Item[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Item> items = Arrays.asList(responseEntity.getBody());
        items.forEach(item -> assertTrue(item.getCurrentPrice() == item.getOriginalPrice()));
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> forEntity = restTemplate.exchange("/items/{id}",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders),
                String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldReturn400NotEnoughItems() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/items/{id}/buy",
                new HttpEntity<>(500, requestHeaders), String.class, 3);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private boolean checkIfItemAndRequestMatch(Item item, ItemRequest request) {
        return request.getName().equals(item.getName()) &&
                request.getStockPrice() == item.getStockPrice() &&
                request.getPrice() == item.getCurrentPrice();
    }
}

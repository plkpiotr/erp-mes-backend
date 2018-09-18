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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BuyItemTest extends FillBaseTemplate {

    private Item item;

    @Before
    public void init() {
        setupToken();
        addManyItemRequests(true);
        item = restTemplate.postForEntity("/items/{id}/supply", new HttpEntity<>(10, requestHeaders),
                Item.class, 2).getBody();
    }

    @Test
    public void checkIfItemQuantityDecreased() {
        int oldQuantity = item.getQuantity();
        ResponseEntity<Item> itemResponseEntity = restTemplate.postForEntity("/items/{id}/buy",
                new HttpEntity<>(4, requestHeaders), Item.class, 2);

        assertThat(itemResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(oldQuantity - itemResponseEntity.getBody().getQuantity()).isEqualTo(4);
    }
}

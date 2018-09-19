package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.shop;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadAllItemsTest extends FillBaseTemplate {

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void checkIfResponseContainsAllItems() {
        ResponseEntity<Item[]> forEntity = restTemplate.exchange("/items", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Item[].class);

        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Item> items = Arrays.asList(forEntity.getBody());
        for(ItemRequest request : itemRequests) {
            assertTrue(items.stream().anyMatch(item -> item.checkIfDataEquals(request.extractItem())));
        }
    }
}

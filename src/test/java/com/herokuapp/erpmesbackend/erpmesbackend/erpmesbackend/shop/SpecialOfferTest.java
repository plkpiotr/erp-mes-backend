package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.shop;

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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpecialOfferTest extends FillBaseTemplate {

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void assertAllItemsAreOffPrice() {
        ResponseEntity<Item[]> responseEntity = restTemplate.postForEntity("/set-special-offer?percentOff=20",
                new HttpEntity<>(null, requestHeaders), Item[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Item> items = Arrays.asList(responseEntity.getBody());
        items.forEach(item -> assertFalse(item.getCurrentPrice() == item.getOriginalPrice()));
    }

    @Test
    public void assertAllItemsAreBackOnPrice() {
        ResponseEntity<Item[]> responseEntity = restTemplate.postForEntity("/cancel-special-offer",
                new HttpEntity<>(null, requestHeaders), Item[].class);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Item> items = Arrays.asList(responseEntity.getBody());
        items.forEach(item -> assertTrue(item.getCurrentPrice() == item.getOriginalPrice()));
    }
}

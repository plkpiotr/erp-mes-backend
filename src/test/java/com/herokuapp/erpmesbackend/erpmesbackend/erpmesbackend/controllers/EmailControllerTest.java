package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailEntity;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
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
public class EmailControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
    }

    @Test
    public void readInboxTest() {
        ResponseEntity<EmailEntity[]> exchange = restTemplate.exchange("/emails/inbox", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmailEntity[].class);

        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getBody()).isNotNull();
    }

    @Test
    public void readOutboxTest() {
        ResponseEntity<EmailEntity[]> exchange = restTemplate.exchange("/emails/outbox", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmailEntity[].class);

        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getBody()).isNotNull();
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> exchange = restTemplate.exchange("/emails/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

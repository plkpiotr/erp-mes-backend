package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.emails;

import com.herokuapp.erpmesbackend.erpmesbackend.emails.EmailEntity;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteEmailTest extends FillBaseTemplate {

    EmailEntity[] entities;

    @Before
    public void init() {
        setupToken();
        addManyEmailEntityRequests(true);
        entities = restTemplate.exchange("/emails/outbox", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmailEntity[].class).getBody();
    }

    @Test
    public void checkIfResponseDoesNotContainDeletedMail() {
        restTemplate.exchange("/emails/{id}", HttpMethod.DELETE,
                new HttpEntity<>(null, requestHeaders), HttpStatus.class, entities[0].getId());

        EmailEntity[] body = restTemplate.exchange("/emails/outbox", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmailEntity[].class).getBody();
        assertTrue(Arrays.stream(body)
                .noneMatch(mail -> mail.getId() == entities[0].getId()));

    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.communication;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailEntity;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendMailTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
        addOneEmailEntityRequest(false);
    }

    @Test
    public void checkIfResponseContainsSentMail() {
        ResponseEntity<EmailEntity> emailEntityResponseEntity = restTemplate.postForEntity("/emails",
                new HttpEntity<>(emailEntityRequest, requestHeaders), EmailEntity.class);
        assertThat(emailEntityResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        List<Long> collect = Arrays.stream(restTemplate.exchange("/emails/outbox", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmailEntity[].class).getBody())
                .map(EmailEntity::getId)
                .collect(Collectors.toList());
        assertTrue(collect.contains(emailEntityResponseEntity.getBody().getId()));
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.emails;

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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadInboxTest extends FillBaseTemplate {

    @Before
    public void init() {
        setupToken();
    }

    @Test
    public void assertResponseContainsReceivedMail() {
        ResponseEntity<EmailEntity[]> exchange = restTemplate.exchange("/emails/inbox", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmailEntity[].class);

        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(exchange.getBody()).isNotNull();
    }
}

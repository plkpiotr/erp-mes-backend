package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.communication;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Notification;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.State;
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
public class UpdateNotificationStateTest extends FillBaseTemplate {

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void checkIfNotificationStateHasChanged() {
        ResponseEntity<Notification> exchange = restTemplate.exchange("/notifications/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Notification.class, 1);

        assertThat(exchange.getBody().getState()).isEqualTo(State.REPORTED);

        ResponseEntity<Notification> notificationResponseEntity = restTemplate.exchange("/notifications/{id}",
                HttpMethod.PUT, new HttpEntity<>(State.IN_PROGRESS.name(), requestHeaders),
                Notification.class, 1);

        assertThat(notificationResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notificationResponseEntity.getBody().getState()).isEqualTo(State.IN_PROGRESS);
    }
}

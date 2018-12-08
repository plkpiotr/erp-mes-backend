package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.NotificationDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Notification;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.State;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.NotificationRequest;
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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotificationControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupNotifications();
    }

    @Test
    public void readAllNotificationsTest() {
        ResponseEntity<NotificationDTO[]> forEntity = restTemplate.exchange("/notifications", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), NotificationDTO[].class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<NotificationDTO> notificationDTOS = Arrays.asList(forEntity.getBody());
        notificationRequests.forEach(request -> notificationDTOS.stream()
                .anyMatch(notificationDTO -> checkIfNotificationDtoAndRequestMatch(notificationDTO, request))
        );
    }

    @Test
    public void readOneNotificationTest() {
        for (int i = 0; i < notificationRequests.size(); i++) {
            ResponseEntity<NotificationDTO> forEntity = restTemplate.exchange("/notifications/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), NotificationDTO.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(forEntity.getBody()).isNotNull();
        }
    }

    @Test
    public void addOneNotificationTest() {
        Long[] ids = {2L, 3L};
        NotificationRequest oneNotificationRequestWithIds = getOneNotificationRequestWithIds(ids);
        ResponseEntity<NotificationDTO> notificationDTOResponseEntity = restTemplate.postForEntity("/notifications",
                new HttpEntity<>(oneNotificationRequestWithIds, requestHeaders), NotificationDTO.class);
        assertThat(notificationDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertTrue(checkIfNotificationDtoAndRequestMatch(notificationDTOResponseEntity.getBody(),
                oneNotificationRequestWithIds));
    }

    @Test
    public void updateNotificationStateTest() {
        ResponseEntity<Notification> exchange = restTemplate.exchange("/notifications/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Notification.class, 1);

        assertThat(exchange.getBody().getState()).isEqualTo(State.REPORTED);

        ResponseEntity<Notification> notificationResponseEntity = restTemplate.exchange("/notifications/{id}",
                HttpMethod.PUT, new HttpEntity<>(State.IN_PROGRESS.name(), requestHeaders),
                Notification.class, 1);

        assertThat(notificationResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(notificationResponseEntity.getBody().getState()).isEqualTo(State.IN_PROGRESS);
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> forEntity = restTemplate.exchange("/notifications/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private boolean checkIfNotificationDtoAndRequestMatch(NotificationDTO notificationDTO, NotificationRequest request) {
        return notificationDTO.getDescription().equals(request.getDescription()) &&
                notificationDTO.getInstruction().equals(request.getInstruction()) &&
                notificationDTO.getConsignees().size() == request.getConsigneeIds().size();
    }
}

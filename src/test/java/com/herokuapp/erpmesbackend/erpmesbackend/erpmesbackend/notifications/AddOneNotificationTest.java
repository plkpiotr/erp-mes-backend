package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.notifications;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.NotificationDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.NotificationRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddOneNotificationTest extends FillBaseTemplate {

    private NotificationDTO notification;

    @Before
    public void init() {
        setupToken();
        addNonAdminRequests(true);

        String instruction = notificationFactory.generateInstruction();
        String description = notificationFactory.generateDescription();

        EmployeeDTO notifier = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody();
        Long notifierId = notifier.getId();

        List<EmployeeDTO> consignees = new ArrayList<>();
        List<Long> consigneeIds = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            EmployeeDTO consignee = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, i).getBody();
            consigneeIds.add(consignee.getId());
            consignees.add(consignee);
        }

        Type type = Type.ORDER;
        Long reference = 1L;

        notificationRequest = new NotificationRequest(instruction, description, notifierId, consigneeIds, type, reference);
        notification = new NotificationDTO(instruction, description, notifier, consignees, type, reference);

    }

    @Test
    public void checkIfResponseContainsAddedNotification() {
        ResponseEntity<NotificationDTO> notificationResponseEntity = restTemplate.postForEntity("/notifications",
                new HttpEntity<>(notificationRequest, requestHeaders), NotificationDTO.class);
        assertThat(notificationResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        NotificationDTO body = notificationResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(notification));
    }
}

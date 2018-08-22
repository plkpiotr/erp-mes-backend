package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.notifications;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.notifications.Notification;
import com.herokuapp.erpmesbackend.erpmesbackend.notifications.NotificationRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.Delivery;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.Type;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddOneNotificationTest extends FillBaseTemplate {

    private Notification notification;

    @Before
    public void init() {
        addOneAdminRequest(true);
        addOneEmployeeRequest(true);
        addOneOrderRequest(true);

        String instruction = notificationFactory.generateInstruction();
        String description = notificationFactory.generateDescription();

        Employee notifier = restTemplate.getForEntity("/employees/{id}", Employee.class, 1).getBody();
        Long notifierId = notifier.getId();

        List<Employee> consignees = new ArrayList<>();
        List<Long> consigneeIds = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Employee consignee = restTemplate.getForEntity("/employees/{id}", Employee.class, i).getBody();
            consigneeIds.add(consignee.getId());
            consignees.add(consignee);
        }

        Type type = Type.ORDER;
        Long reference = 1L;

        notificationRequest = new NotificationRequest(instruction, description, notifierId,
                consigneeIds, type, reference);

        notification = new Notification(instruction, description, notifier, consignees, type, reference);

    }

    @Test
    public void checkIfResponseContainsAddedNotification() {
        ResponseEntity<Notification> notificationResponseEntity = restTemplate.postForEntity("/notifications",
                notificationRequest, Notification.class);
        assertThat(notificationResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Notification body = notificationResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(notification));
    }
}

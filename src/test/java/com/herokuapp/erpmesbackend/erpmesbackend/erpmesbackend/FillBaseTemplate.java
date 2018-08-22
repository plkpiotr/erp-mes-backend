package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.Holiday;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.HolidayFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.holidays.HolidayRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.notifications.Notification;
import com.herokuapp.erpmesbackend.erpmesbackend.notifications.NotificationFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.notifications.NotificationRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.Item;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.ItemRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.Delivery;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryItemRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.OrderFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.OrderRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.Status;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.Suggestion;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.SuggestionFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.SuggestionRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class FillBaseTemplate {

    @Autowired
    protected TestRestTemplate restTemplate;

    protected EmployeeRequest employeeRequest;
    protected EmployeeRequest adminRequest;
    protected EmployeeRequest nonAdminRequest;

    protected TaskRequest taskRequest;
    protected HolidayRequest holidayRequest;
    protected ItemRequest itemRequest;
    protected DeliveryRequest deliveryRequest;
    protected OrderRequest orderRequest;
    protected NotificationRequest notificationRequest;
    protected SuggestionRequest suggestionRequest;

    protected List<EmployeeRequest> employeeRequests;
    protected List<EmployeeRequest> adminRequests;
    protected List<EmployeeRequest> nonAdminRequests;
    protected List<TaskRequest> taskRequests;
    protected List<HolidayRequest> holidayRequests;
    protected List<ItemRequest> itemRequests;
    protected List<DeliveryRequest> deliveryRequests;
    protected List<OrderRequest> orderRequests;
    protected List<NotificationRequest> notificationRequests;
    protected List<SuggestionRequest> suggestionRequests;

    protected EmployeeFactory employeeFactory;
    protected TaskFactory taskFactory;
    protected HolidayFactory holidayFactory;
    protected NotificationFactory notificationFactory;
    protected SuggestionFactory suggestionFactory;
    protected OrderFactory orderFactory;

    public FillBaseTemplate() {
        employeeFactory = new EmployeeFactory();
        taskFactory = new TaskFactory();
        holidayFactory = new HolidayFactory();
        notificationFactory = new NotificationFactory();
        suggestionFactory = new SuggestionFactory();
        orderFactory = new OrderFactory();

        employeeRequests = new ArrayList<>();
        adminRequests = new ArrayList<>();
        nonAdminRequests = new ArrayList<>();
        taskRequests = new ArrayList<>();
        holidayRequests = new ArrayList<>();
        itemRequests = new ArrayList<>();
        deliveryRequests = new ArrayList<>();
        notificationRequests = new ArrayList<>();
        suggestionRequests = new ArrayList<>();
    }

    protected void addOneEmployeeRequest(boolean shouldPost) {
        employeeRequest = employeeFactory.generateEmployeeRequest();
        if (shouldPost) {
            restTemplate.postForEntity("/employees", employeeRequest, String.class);
        }
    }

    protected void addOneAdminRequest(boolean shouldPost) {
        adminRequest = employeeFactory.generateAdminRequest();
        if (shouldPost) {
            restTemplate.postForEntity("/employees", adminRequest, String.class);
        }
    }

    protected void addOneNonAdminRequest(boolean shouldPost) {
        nonAdminRequest = employeeFactory.generateNonAdminRequest();
        if (shouldPost) {
            restTemplate.postForEntity("/employees", nonAdminRequest, String.class);
        }
    }

    protected void addEmployeeRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            employeeRequests.add(employeeFactory.generateEmployeeRequest());
        }
        if (shouldPost) {
            employeeRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    request, String.class));
        }
    }

    protected void addAdminRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            adminRequests.add(employeeFactory.generateAdminRequest());
        }
        if (shouldPost) {
            adminRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    request, Employee.class));
        }
    }

    protected void addNonAdminRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            nonAdminRequests.add(employeeFactory.generateNonAdminRequest());
        }
        if (shouldPost) {
            nonAdminRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    request, Employee.class));
        }
    }

    protected void addOneHolidayRequest(long employeeId, boolean shouldPost) {
        holidayRequest = holidayFactory.generateHolidayRequest();
        if(shouldPost) {
            restTemplate.postForEntity("/employees/{id}/holidays", holidayRequest,
                    Holiday.class, employeeId);
        }
    }

    protected Task addOneTaskRequest(boolean shouldPost) {
        String name = taskFactory.generateName();
        Long assigneeId = 1L;
        List<Long> precedingTasksIds = new ArrayList<>();
        String details = taskFactory.generateDetails();
        Integer estimatedTimeInMinutes = taskFactory.generateEstimatedTimeInMinutes();
        LocalDateTime deadline = taskFactory.generateDeadline(); // TODO: FILL ORDERS (TYPE + REFERENCE)
        LocalDateTime scheduledTime = taskFactory.generateScheduledTime();

        TaskRequest taskRequest = new TaskRequest(name, assigneeId, precedingTasksIds, details,
                estimatedTimeInMinutes, deadline, null, null, scheduledTime);

        if (shouldPost)
            restTemplate.postForEntity("/tasks", taskRequest, Task.class);

        Employee assignee = restTemplate.getForEntity("/employees/{id}", Employee.class, 1).getBody();

        List<Task> precedingTasks = new ArrayList<>();
        return new Task(name, Category.TODO, assignee, precedingTasks, details, estimatedTimeInMinutes, deadline, null,
                null, scheduledTime);
    }

    protected List<Task> addTaskRequests(boolean shouldPost) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            taskRequests.add(new TaskRequest());
            tasks.add(addOneTaskRequest(shouldPost));
        }
        return tasks;
    }

    protected void addManyHolidayRequests(long employeeId, boolean shouldPost) {
        for(int i = 0; i < 4; i++) {
            holidayRequests.add(holidayFactory.generateHolidayRequest());        }
        if(shouldPost) {
            holidayRequests.forEach(request -> restTemplate.postForEntity("/employees/{id}/holidays",
                    request, Holiday.class, employeeId));
        }
    }

    protected void addOneItemRequest(boolean shouldPost) {
        itemRequest = new ItemRequest("Random name", 8.00, 10.00);
        if(shouldPost) {
            restTemplate.postForEntity("/items", itemRequest, Item.class);
        }
    }

    protected void addManyItemRequests(boolean shouldPost) {
        for(int i = 0; i < 10; i++) {
            itemRequests.add(new ItemRequest("Random name" + i, i*2+5, i*3+5));
        }
        if(shouldPost) {
            itemRequests.forEach(request -> restTemplate.postForEntity("/items", request, Item.class));
        }
    }

    protected void addOneDeliveryRequest(boolean shouldPost) {
        List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            deliveryItemRequests.add(new DeliveryItemRequest(i+1, i+10));
        }
        deliveryRequest = new DeliveryRequest(deliveryItemRequests, LocalDate.now().plusDays(3));
        if(shouldPost) {
            restTemplate.postForEntity("/deliveries", deliveryRequest, Delivery.class);
        }
    }

    protected void addManyDeliveryRequests(boolean shouldPost) {
        for(int i = 0; i < 3; i++) {
            List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
            for(int j = 0; j < 3; j++) {
                deliveryItemRequests.add(new DeliveryItemRequest(i+j+1, j+10));
            }
            deliveryRequests.add(new DeliveryRequest(deliveryItemRequests, LocalDate.now().plusDays(3)));
        }
        if(shouldPost) {
            deliveryRequests.forEach(request -> restTemplate.postForEntity("/deliveries", request, Delivery.class));
        }
    }

    protected void addOneOrderRequest(boolean shouldPost) {
        List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();

        for (int i = 0; i < 3; i++)
            deliveryItemRequests.add(new DeliveryItemRequest(i + 1, i + 10));

        String firstName = orderFactory.generateFirstName();
        String lastName = orderFactory.generateLastName();
        String email = orderFactory.generateEmail();
        String phoneNumber = orderFactory.generatePhoneNumber();
        String street = orderFactory.generateStreet();
        String houseNumber = orderFactory.generateHouseNumber();
        String city = orderFactory.generateCity();
        String postalCode = orderFactory.generatePostalCode();

        orderRequest= new OrderRequest(firstName, lastName, email, phoneNumber, street, houseNumber, city,
                postalCode, deliveryItemRequests, LocalDate.now().plusDays(3));

        if (shouldPost)
            restTemplate.postForEntity("/orders", orderRequest, Order.class);
    }

    protected void addManyOrderRequests(boolean shouldPost) {
        for (int i = 0; i < 3; i++) {
            List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
            for (int j = 0; j < 3; j++)
                deliveryItemRequests.add(new DeliveryItemRequest(i + j + 1, j + 10));

            String firstName = orderFactory.generateFirstName();
            String lastName = orderFactory.generateLastName();
            String email = orderFactory.generateEmail();
            String phoneNumber = orderFactory.generatePhoneNumber();
            String street = orderFactory.generateStreet();
            String houseNumber = orderFactory.generateHouseNumber();
            String city = orderFactory.generateCity();
            String postalCode = orderFactory.generatePostalCode();

            orderRequest= new OrderRequest(firstName, lastName, email, phoneNumber, street, houseNumber,
                    city, postalCode, deliveryItemRequests, LocalDate.now().plusDays(3));
        }

        if (shouldPost)
            orderRequests.forEach(orderRequest -> restTemplate.postForEntity("/orders", orderRequest, Order.class));
    }

    protected Notification addOneNotificationRequest(boolean shouldPost) {
        String instruction = notificationFactory.generateInstruction();
        String description = notificationFactory.generateDescription();
        Long notifierId = 1L;

        List<Long> consigneeIds = new ArrayList<>();
        consigneeIds.add(1L);
        consigneeIds.add(2L);

        Type type = Type.ORDER;
        Long reference = 1L;

        NotificationRequest notificationRequest = new NotificationRequest(instruction, description, notifierId,
                consigneeIds, type, reference);

        if (shouldPost)
            restTemplate.postForEntity("/notifications", notificationRequest, Notification.class);

        Employee notifier = restTemplate.getForEntity("/employees/{id}", Employee.class, 1).getBody();

        List<Employee> consignees = new ArrayList<>();
        consignees.add(restTemplate.getForEntity("/employees/{id}", Employee.class, 1).getBody());
        consignees.add(restTemplate.getForEntity("/employees/{id}", Employee.class, 2).getBody());

        return new Notification(instruction, description, notifier, consignees, type, reference);
    }

    protected List<Notification> addNotificationRequests(boolean shouldPost) {
        List<Notification> notifications = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            notificationRequests.add(new NotificationRequest());
            notifications.add(addOneNotificationRequest(shouldPost));
        }
        return notifications;
    }

    protected Suggestion addOneSuggestionRequest(boolean shouldPost) {
        String name = suggestionFactory.generateName();
        String description = suggestionFactory.generateDescription();
        Long authorId = 1L;

        List<Long> recipientIds = new ArrayList<>();
        recipientIds.add(1L);
        recipientIds.add(2L);

        SuggestionRequest suggestionRequest = new SuggestionRequest(name, description, authorId, recipientIds);

        if (shouldPost)
            restTemplate.postForEntity("/suggestions", suggestionRequest, Suggestion.class);

        Employee author = restTemplate.getForEntity("/employees/{id}", Employee.class, 1).getBody();

        List<Employee> recipients = new ArrayList<>();
        recipients.add(restTemplate.getForEntity("/employees/{id}", Employee.class, 1).getBody());
        recipients.add(restTemplate.getForEntity("/employees/{id}", Employee.class, 2).getBody());

        return new Suggestion(name, description, author, recipients);
    }

    protected List<Suggestion> addSuggestionRequests(boolean shouldPost) {
        List<Suggestion> suggestions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            suggestionRequests.add(new SuggestionRequest());
            suggestions.add(addOneSuggestionRequest(shouldPost));
        }
        return suggestions;
    }
}

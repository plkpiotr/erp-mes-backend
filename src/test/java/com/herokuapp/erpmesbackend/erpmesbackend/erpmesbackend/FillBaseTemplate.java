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
import com.herokuapp.erpmesbackend.erpmesbackend.planning.SpecialPlan;
import com.herokuapp.erpmesbackend.erpmesbackend.planning.SpecialPlanRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.security.Credentials;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.Item;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.ItemRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.Delivery;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryItemRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.OrderFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.ShopServiceRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.Suggestion;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.SuggestionFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.SuggestionRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.tasks.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class FillBaseTemplate {

    @Autowired
    protected TestRestTemplate restTemplate;

    protected HttpHeaders requestHeaders;

    protected EmployeeRequest employeeRequest;
    protected EmployeeRequest adminRequest;
    protected EmployeeRequest nonAdminRequest;

    protected TaskRequest taskRequest;
    protected HolidayRequest holidayRequest;
    protected ItemRequest itemRequest;
    protected DeliveryRequest deliveryRequest;
    protected ShopServiceRequest orderRequest;
    protected NotificationRequest notificationRequest;
    protected SuggestionRequest suggestionRequest;
    protected SpecialPlanRequest specialPlanRequest;

    protected List<EmployeeRequest> employeeRequests;
    protected List<EmployeeRequest> adminRequests;
    protected List<EmployeeRequest> nonAdminRequests;
    protected List<TaskRequest> taskRequests;
    protected List<HolidayRequest> holidayRequests;
    protected List<ItemRequest> itemRequests;
    protected List<DeliveryRequest> deliveryRequests;
    protected List<ShopServiceRequest> orderRequests;
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
        orderRequests = new ArrayList<>();
        notificationRequests = new ArrayList<>();
        suggestionRequests = new ArrayList<>();
    }

    protected void setupToken() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/generate-token",
                new Credentials("szef.ceo@company.com",
                        "haslo123"), String.class);
        String body = responseEntity.getBody();
        String replace = body.replace("{\"token\":\"", "");
        String replace1 = replace.replace("\"}", "");
        requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        requestHeaders.add("Authorization", "Bearer " + replace1);
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
            setupToken();
            restTemplate.postForEntity("/employees", new HttpEntity<>(adminRequest, requestHeaders),
                    String.class);
        }
    }

    protected void addOneNonAdminRequest(boolean shouldPost) {
        nonAdminRequest = employeeFactory.generateNonAdminRequest();
        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/employees", new HttpEntity<>(nonAdminRequest, requestHeaders),
                    String.class);
        }
    }

    protected void addEmployeeRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            employeeRequests.add(employeeFactory.generateEmployeeRequest());
        }
        if (shouldPost) {
            setupToken();
            employeeRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    new HttpEntity<>(request, requestHeaders), String.class));
        }
    }

    protected void addAdminRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            adminRequests.add(employeeFactory.generateAdminRequest());
        }
        if (shouldPost) {
            setupToken();
            adminRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    new HttpEntity<>(request, requestHeaders), Employee.class));
        }
    }

    protected void addNonAdminRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            nonAdminRequests.add(employeeFactory.generateNonAdminRequest());
        }
        if (shouldPost) {
            setupToken();
            nonAdminRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    new HttpEntity<>(request, requestHeaders), Employee.class));
        }
    }

    protected void addOneHolidayRequest(long employeeId, boolean shouldPost) {
        holidayRequest = holidayFactory.generateHolidayRequest();
        if(shouldPost) {
            setupToken();
            restTemplate.postForEntity("/employees/{id}/holidays", new HttpEntity<>(holidayRequest,
                    requestHeaders), Holiday.class, employeeId);
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

        setupToken();
        if (shouldPost) {
            restTemplate.postForEntity("/tasks", new HttpEntity<>(taskRequest, requestHeaders), Task.class);
        }

        Employee assignee = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee.class, 1).getBody();

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
            setupToken();
            holidayRequests.forEach(request -> restTemplate.postForEntity("/employees/{id}/holidays",
                    new HttpEntity<>(request, requestHeaders), Holiday.class, employeeId));
        }
    }

    protected void addOneItemRequest(boolean shouldPost) {
        itemRequest = new ItemRequest("Random name", 8.00, 10.00);
        if(shouldPost) {
            setupToken();
            restTemplate.postForEntity("/items", new HttpEntity<>(itemRequest, requestHeaders), Item.class);
        }
    }

    protected void addManyItemRequests(boolean shouldPost) {
        for(int i = 0; i < 10; i++) {
            itemRequests.add(new ItemRequest("Random name" + i, i*2+5, i*3+5));
        }
        if(shouldPost) {
            setupToken();
            itemRequests.forEach(request -> restTemplate.postForEntity("/items", new HttpEntity<>(request,
                    requestHeaders), Item.class));
        }
    }

    protected void addOneDeliveryRequest(boolean shouldPost) {
        List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
        for(int i = 0; i < 3; i++) {
            deliveryItemRequests.add(new DeliveryItemRequest(i+1, i+10));
        }
        deliveryRequest = new DeliveryRequest(deliveryItemRequests, LocalDate.now().plusDays(3));
        if(shouldPost) {
            setupToken();
            restTemplate.postForEntity("/deliveries", new HttpEntity<>(deliveryRequest, requestHeaders),
                    Delivery.class);
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
            setupToken();
            deliveryRequests.forEach(request -> restTemplate.postForEntity("/deliveries",
                    new HttpEntity<>(request, requestHeaders), Delivery.class));
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

        orderRequest = new ShopServiceRequest(firstName, lastName, email, phoneNumber, street, houseNumber, city,
                postalCode, deliveryItemRequests, LocalDate.now().plusDays(3), null);

        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/orders", new HttpEntity<>(orderRequest, requestHeaders), Order.class);
        }
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

            orderRequests.add(new ShopServiceRequest(firstName, lastName, email, phoneNumber, street, houseNumber,
                    city, postalCode, deliveryItemRequests, LocalDate.now().plusDays(3), null));
        }

        if (shouldPost) {
            setupToken();
            orderRequests.forEach(orderRequest -> restTemplate.postForEntity("/orders",
                    new HttpEntity<>(orderRequest, requestHeaders), Order.class));
        }
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

        setupToken();
        if (shouldPost) {
            restTemplate.postForEntity("/notifications", new HttpEntity<>(notificationRequest, requestHeaders),
                    Notification.class);
        }

        Employee notifier = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee.class, 1).getBody();

        List<Employee> consignees = new ArrayList<>();
        consignees.add(restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee.class, 1).getBody());
        consignees.add(restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee.class, 2).getBody());

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

        setupToken();
        if (shouldPost) {
            restTemplate.postForEntity("/suggestions", new HttpEntity<>(suggestionRequest, requestHeaders),
                    Suggestion.class);
        }

        Employee author = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee.class, 1).getBody();

        List<Employee> recipients = new ArrayList<>();
        recipients.add(restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee.class, 1).getBody());
        recipients.add(restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Employee.class, 2).getBody());

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

    protected void addSpecialPlanRequest(boolean shouldPost) {
        specialPlanRequest = new SpecialPlanRequest("Special plan", LocalDate.now().plusDays(3),
                20, 50, 10, 10);
        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/special-plan", new HttpEntity<>(specialPlanRequest, requestHeaders),
                    SpecialPlan.class);
        }
    }
}

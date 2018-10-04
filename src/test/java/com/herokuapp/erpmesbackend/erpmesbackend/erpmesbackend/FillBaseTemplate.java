package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.ChannelDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.MessageDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.NotificationDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.SuggestionDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.factory.*;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailEntity;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.*;
import com.herokuapp.erpmesbackend.erpmesbackend.production.dto.TaskDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.production.factory.TaskFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.SpecialPlan;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.SpecialPlanRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.TaskRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.security.Credentials;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.factory.ShopServiceFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.*;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.DeliveryItemRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.DeliveryRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.ItemRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.ShopServiceRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.factory.EmployeeFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.factory.HolidayFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Holiday;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.HolidayRequest;
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
    protected ShopServiceRequest returnRequest;
    protected ShopServiceRequest complaintRequest;
    protected EmailEntityRequest emailEntityRequest;
    protected ChannelRequest channelRequest;
    protected MessageRequest messageRequest;

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
    protected List<ShopServiceRequest> returnRequests;
    protected List<ShopServiceRequest> complaintRequests;
    protected List<EmailEntityRequest> emailEntityRequests;
    protected List<ChannelRequest> channelRequests;
    protected List<MessageRequest> messageRequests;

    protected EmployeeFactory employeeFactory;
    protected TaskFactory taskFactory;
    protected HolidayFactory holidayFactory;
    protected NotificationFactory notificationFactory;
    protected SuggestionFactory suggestionFactory;
    protected ShopServiceFactory shopServiceFactory;
    protected EmailFactory emailFactory;
    protected ChannelFactory channelFactory;
    protected MessageFactory messageFactory;

    public FillBaseTemplate() {
        employeeFactory = new EmployeeFactory();
        taskFactory = new TaskFactory();
        holidayFactory = new HolidayFactory();
        notificationFactory = new NotificationFactory();
        suggestionFactory = new SuggestionFactory();
        shopServiceFactory = new ShopServiceFactory();
        emailFactory = new EmailFactory();
        channelFactory = new ChannelFactory();
        messageFactory = new MessageFactory();

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
        returnRequests = new ArrayList<>();
        complaintRequests = new ArrayList<>();
        emailEntityRequests = new ArrayList<>();
        channelRequests = new ArrayList<>();
        messageRequests = new ArrayList<>();
    }

    protected void init() {
        setupToken();
        if (emptyResponse("/employees/3")) {
            addEmployeeRequests(true);
        }
        if (emptyResponse("/items/2")) {
            addManyItemRequests(true);
        }
        if (emptyResponse("/complaints/2")) {
            addManyComplaintRequests(true);
        }
        if (emptyResponse("/deliveries/2")) {
            addManyDeliveryRequests(true);
        }
        if (emptyResponse("/orders/2")) {
            addManyOrderRequests(true);
        }
        if (emptyResponse("/returns/2")) {
            addManyReturnRequests(true);
        }
        if (emptyResponse("/tasks/2")) {
            addTaskRequests(true);
        }
        if (emptyResponse("/channels/2")) {
            addChannelRequests(true);
        }
        if (emptyResponse("/notifications/2")) {
            addNotificationRequests(true);
        }
        if (emptyResponse("/suggestions/2")) {
            addSuggestionRequests(true);
        }
        if (outboxEmpty()) {
            addManyEmailEntityRequests(true);
        }
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

    private boolean emptyResponse(String url) {
        return restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(null, requestHeaders), String.class)
                .getStatusCode().equals(HttpStatus.NOT_FOUND);
    }

    private boolean outboxEmpty() {
        return restTemplate.exchange("/emails/outbox", HttpMethod.GET, new HttpEntity<>(null, requestHeaders),
                EmailEntity[].class).getBody().length <= 2;
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
                    new HttpEntity<>(request, requestHeaders), String.class));
        }
    }

    protected void addNonAdminRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            nonAdminRequests.add(employeeFactory.generateNonAdminRequest());
        }
        if (shouldPost) {
            setupToken();
            nonAdminRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    new HttpEntity<>(request, requestHeaders), String.class));
        }
    }

    protected void addOneHolidayRequest(long employeeId, boolean shouldPost) {
        holidayRequest = holidayFactory.generateHolidayRequest();
        holidayRequest.setDuration(1);
        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/employees/{id}/holidays", new HttpEntity<>(holidayRequest,
                    requestHeaders), Holiday.class, employeeId);
        }
    }

    protected TaskDTO addOneTaskRequest(boolean shouldPost) {
        String name = taskFactory.generateName();
        List<Long> precedingTasksIds = new ArrayList<>();
        Long assigneeId = 2L;
        Integer estimatedTime = taskFactory.generateEstimatedTime();
        LocalDateTime deadline = taskFactory.generateDeadline();
        String details = taskFactory.generateDetails();

        EmployeeDTO assigneeDTO = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 2).getBody();

        EmployeeDTO authorDTO = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody();

        TaskRequest taskRequest = new TaskRequest(name, precedingTasksIds, assigneeId, estimatedTime, deadline,
                null, details, null);

        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/tasks", new HttpEntity<>(taskRequest, requestHeaders), TaskDTO.class);
        }

        List<TaskDTO> precedingTaskDTOs = new ArrayList<>();
        return new TaskDTO(name, precedingTasksIds, authorDTO, assigneeDTO, estimatedTime);
    }

    protected List<TaskDTO> addTaskRequests(boolean shouldPost) {
        List<TaskDTO> taskDTOs = new ArrayList<>();
        for (int i = 1; i <= 8; i++) {
            taskRequests.add(new TaskRequest());
            taskDTOs.add(addOneTaskRequest(shouldPost));
        }
        return taskDTOs;
    }

    protected void addManyHolidayRequests(long employeeId, boolean shouldPost) {
        for (int i = 0; i < 4; i++) {
            holidayRequests.add(holidayFactory.generateHolidayRequest());
        }
        if (shouldPost) {
            setupToken();
            holidayRequests.forEach(request -> restTemplate.postForEntity("/employees/{id}/holidays",
                    new HttpEntity<>(request, requestHeaders), Holiday.class, employeeId));
        }
    }

    protected void addOneItemRequest(boolean shouldPost) {
        itemRequest = new ItemRequest("Random name", 8.00, 10.00);
        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/items", new HttpEntity<>(itemRequest, requestHeaders), Item.class);
        }
    }

    protected void addManyItemRequests(boolean shouldPost) {
        for (int i = 0; i < 10; i++) {
            itemRequests.add(new ItemRequest("Random name" + i, i * 2 + 5, i * 3 + 5));
        }
        if (shouldPost) {
            setupToken();
            itemRequests.forEach(request -> restTemplate.postForEntity("/items", new HttpEntity<>(request,
                    requestHeaders), Item.class));
        }
    }

    protected void addOneDeliveryRequest(boolean shouldPost) {
        List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            deliveryItemRequests.add(new DeliveryItemRequest(i + 1, i + 10));
        }
        deliveryRequest = new DeliveryRequest(deliveryItemRequests, LocalDate.now().plusDays(3));
        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/deliveries", new HttpEntity<>(deliveryRequest, requestHeaders),
                    Delivery.class);
        }
    }

    protected void addManyDeliveryRequests(boolean shouldPost) {
        for (int i = 0; i < 3; i++) {
            List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                deliveryItemRequests.add(new DeliveryItemRequest(i + j + 1, j + 10));
            }
            deliveryRequests.add(new DeliveryRequest(deliveryItemRequests, LocalDate.now().plusDays(3)));
        }
        if (shouldPost) {
            setupToken();
            deliveryRequests.forEach(request -> restTemplate.postForEntity("/deliveries",
                    new HttpEntity<>(request, requestHeaders), Delivery.class));
        }
    }

    protected void addOneOrderRequest(boolean shouldPost) {
        List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();

        for (int i = 0; i < 3; i++)
            deliveryItemRequests.add(new DeliveryItemRequest(i + 1, i + 10));

        String firstName = shopServiceFactory.generateFirstName();
        String lastName = shopServiceFactory.generateLastName();
        String email = shopServiceFactory.generateEmail();
        String phoneNumber = shopServiceFactory.generatePhoneNumber();
        String street = shopServiceFactory.generateStreet();
        String houseNumber = shopServiceFactory.generateHouseNumber();
        String city = shopServiceFactory.generateCity();
        String postalCode = shopServiceFactory.generatePostalCode();

        orderRequest = new ShopServiceRequest(firstName, lastName, email, phoneNumber, street, houseNumber, city,
                postalCode, deliveryItemRequests, LocalDate.now().plusDays(3), null, null);

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

            String firstName = shopServiceFactory.generateFirstName();
            String lastName = shopServiceFactory.generateLastName();
            String email = shopServiceFactory.generateEmail();
            String phoneNumber = shopServiceFactory.generatePhoneNumber();
            String street = shopServiceFactory.generateStreet();
            String houseNumber = shopServiceFactory.generateHouseNumber();
            String city = shopServiceFactory.generateCity();
            String postalCode = shopServiceFactory.generatePostalCode();

            orderRequests.add(new ShopServiceRequest(firstName, lastName, email, phoneNumber, street, houseNumber,
                    city, postalCode, deliveryItemRequests, LocalDate.now().plusDays(3), null, null));
        }

        if (shouldPost) {
            setupToken();
            orderRequests.forEach(orderRequest -> restTemplate.postForEntity("/orders",
                    new HttpEntity<>(orderRequest, requestHeaders), Order.class));
        }
    }

    protected NotificationDTO addOneNotificationRequest(boolean shouldPost) {
        String instruction = notificationFactory.generateInstruction();
        String description = notificationFactory.generateDescription();

        List<Long> consigneeIds = new ArrayList<>();
        consigneeIds.add(1L);
        consigneeIds.add(2L);

        Type type = Type.ORDER;

        NotificationRequest notificationRequest = new NotificationRequest(instruction, description, consigneeIds, type);

        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/notifications", new HttpEntity<>(notificationRequest, requestHeaders),
                    NotificationDTO.class);
        }

        EmployeeDTO notifier = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody();

        List<EmployeeDTO> consignees = new ArrayList<>();
        consignees.add(restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody());
        consignees.add(restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 2).getBody());

        return new NotificationDTO(instruction, description, notifier, consignees, type);
    }

    protected List<NotificationDTO> addNotificationRequests(boolean shouldPost) {
        List<NotificationDTO> notifications = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            notificationRequests.add(new NotificationRequest());
            notifications.add(addOneNotificationRequest(shouldPost));
        }
        return notifications;
    }

    protected SuggestionDTO addOneSuggestionRequest(boolean shouldPost) {
        String name = suggestionFactory.generateName();
        String description = suggestionFactory.generateDescription();
        Long authorId = 1L;

        List<Long> recipientIds = new ArrayList<>();
        recipientIds.add(1L);
        recipientIds.add(2L);

        SuggestionRequest suggestionRequest = new SuggestionRequest(name, description, recipientIds);

        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/suggestions", new HttpEntity<>(suggestionRequest, requestHeaders),
                    SuggestionDTO.class);
        }

        EmployeeDTO authorDTO = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody();

        List<EmployeeDTO> recipientDTOs = new ArrayList<>();
        recipientDTOs.add(restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody());
        recipientDTOs.add(restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 2).getBody());

        return new SuggestionDTO(name, description, authorDTO, recipientDTOs);
    }

    protected List<SuggestionDTO> addSuggestionRequests(boolean shouldPost) {
        List<SuggestionDTO> suggestions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            suggestionRequests.add(new SuggestionRequest());
            suggestions.add(addOneSuggestionRequest(shouldPost));
        }
        return suggestions;
    }

    protected ChannelDTO addOneChannelRequest(boolean shouldPost) {
        String name = channelFactory.generateName();
        List<Long> participantsIds = new ArrayList<>();
        participantsIds.add(1L);
        participantsIds.add(2L);

        ChannelRequest channelRequest = new ChannelRequest(name, participantsIds);

        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/channels", new HttpEntity<>(channelRequest, requestHeaders),
                    ChannelDTO.class);
        }

        List<EmployeeDTO> recipientDTOs = new ArrayList<>();
        recipientDTOs.add(restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody());
        recipientDTOs.add(restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 2).getBody());

        return new ChannelDTO(name, recipientDTOs);
    }

    protected List<ChannelDTO> addChannelRequests(boolean shouldPost) {
        List<ChannelDTO> channelDTOs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            channelRequests.add(new ChannelRequest());
            channelDTOs.add(addOneChannelRequest(shouldPost));
        }
        return channelDTOs;
    }

    protected MessageDTO addOneMessageRequest(boolean shouldPost) {
        String content = messageFactory.generateContent();

        Long authorId = 1L;
        EmployeeDTO authorDTO = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody();

        addOneChannelRequest(true);
        Long channelId = 1L;

        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/messages/1", new HttpEntity<>(messageRequest, requestHeaders),
                    MessageDTO.class);
        }

        MessageRequest messageRequest = new MessageRequest(content);
        return new MessageDTO(content, authorDTO, channelId);
    }

    protected List<MessageDTO> addMessageRequests(boolean shouldPost) {
        List<MessageDTO> messageDTOs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            messageRequests.add(new MessageRequest());
            messageDTOs.add(addOneMessageRequest(shouldPost));
        }
        return messageDTOs;
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

    protected void addOneReturnRequest(boolean shouldPost) {
        List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();

        for (int i = 0; i < 3; i++)
            deliveryItemRequests.add(new DeliveryItemRequest(i + 1, i + 10));

        returnRequest = new ShopServiceRequest(shopServiceFactory.generateFirstName(),
                shopServiceFactory.generateLastName(), shopServiceFactory.generateEmail(),
                shopServiceFactory.generatePhoneNumber(), shopServiceFactory.generateStreet(),
                shopServiceFactory.generateHouseNumber(), shopServiceFactory.generateCity(),
                shopServiceFactory.generatePostalCode(), deliveryItemRequests,
                LocalDate.now().plusDays(3), null, null);

        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/returns", new HttpEntity<>(returnRequest, requestHeaders),
                    Return.class);
        }
    }

    protected void addManyReturnRequests(boolean shouldPost) {
        for (int i = 0; i < 3; i++) {
            List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
            for (int j = 0; j < 3; j++)
                deliveryItemRequests.add(new DeliveryItemRequest(i + j + 1, j + 10));

            returnRequests.add(new ShopServiceRequest(shopServiceFactory.generateFirstName(),
                    shopServiceFactory.generateLastName(), shopServiceFactory.generateEmail(),
                    shopServiceFactory.generatePhoneNumber(), shopServiceFactory.generateStreet(),
                    shopServiceFactory.generateHouseNumber(), shopServiceFactory.generateCity(),
                    shopServiceFactory.generatePostalCode(), deliveryItemRequests,
                    LocalDate.now().plusDays(3), null, null));
        }

        if (shouldPost) {
            setupToken();
            returnRequests.forEach(request -> restTemplate.postForEntity("/returns",
                    new HttpEntity<>(request, requestHeaders), Return.class));
        }
    }

    protected void addOneComplaintRequest(boolean shouldPost) {
        List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();

        for (int i = 0; i < 3; i++)
            deliveryItemRequests.add(new DeliveryItemRequest(i + 1, i + 10));

        complaintRequest = new ShopServiceRequest(shopServiceFactory.generateFirstName(),
                shopServiceFactory.generateLastName(), shopServiceFactory.generateEmail(),
                shopServiceFactory.generatePhoneNumber(), shopServiceFactory.generateStreet(),
                shopServiceFactory.generateHouseNumber(), shopServiceFactory.generateCity(),
                shopServiceFactory.generatePostalCode(), deliveryItemRequests,
                LocalDate.now().plusDays(3), shopServiceFactory.generateResolution(), "Random fault");

        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/complaints", new HttpEntity<>(complaintRequest, requestHeaders),
                    Complaint.class);
        }
    }

    protected void addManyComplaintRequests(boolean shouldPost) {
        for (int i = 0; i < 3; i++) {
            List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
            for (int j = 0; j < 3; j++)
                deliveryItemRequests.add(new DeliveryItemRequest(i + j + 1, j + 10));

            complaintRequests.add(new ShopServiceRequest(shopServiceFactory.generateFirstName(),
                    shopServiceFactory.generateLastName(), shopServiceFactory.generateEmail(),
                    shopServiceFactory.generatePhoneNumber(), shopServiceFactory.generateStreet(),
                    shopServiceFactory.generateHouseNumber(), shopServiceFactory.generateCity(),
                    shopServiceFactory.generatePostalCode(), deliveryItemRequests,
                    LocalDate.now().plusDays(3), shopServiceFactory.generateResolution(), "Random fault"));
        }

        if (shouldPost) {
            setupToken();
            complaintRequests.forEach(request -> restTemplate.postForEntity("/complaints",
                    new HttpEntity<>(request, requestHeaders), Complaint.class));
        }
    }

    protected void addOneEmailEntityRequest(boolean shouldPost) {
        emailEntityRequest = emailFactory.generateRequestWithAddress();
        if (shouldPost) {
            restTemplate.postForEntity("/emails", new HttpEntity<>(emailEntityRequest, requestHeaders),
                    EmailEntity.class);
        }
    }

    protected void addManyEmailEntityRequests(boolean shouldPost) {
        for (int i = 0; i < 3; i++) {
            emailEntityRequests.add(emailFactory.generateRequestWithAddress());
        }
        if (shouldPost) {
            emailEntityRequests.forEach(request -> restTemplate.postForEntity("/emails",
                    new HttpEntity<>(request, requestHeaders), EmailEntity.class));
        }
    }
}

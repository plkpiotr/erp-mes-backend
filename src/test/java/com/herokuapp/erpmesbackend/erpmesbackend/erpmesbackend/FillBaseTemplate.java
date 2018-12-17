package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.factory.EmailFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.factory.NotificationFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.factory.SuggestionFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailEntity;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.EmailEntityRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.NotificationRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.SuggestionRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.production.factory.TaskFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.production.request.TaskRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.security.Credentials;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.factory.ShopServiceFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Item;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.DeliveryRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.ItemRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.ShopServiceRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.factory.EmployeeFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.factory.HolidayFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Holiday;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.AdminRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.ContractRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.HolidayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class FillBaseTemplate {

    @Autowired
    protected TestRestTemplate restTemplate;

    protected HttpHeaders requestHeaders;

    protected EmployeeRequest adminRequest;
    protected EmployeeRequest nonAdminRequest;

    protected HolidayRequest holidayRequest;
    protected EmailEntityRequest emailEntityRequest;

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

    protected EmployeeFactory employeeFactory;
    protected TaskFactory taskFactory;
    protected HolidayFactory holidayFactory;
    protected NotificationFactory notificationFactory;
    protected SuggestionFactory suggestionFactory;
    protected ShopServiceFactory shopServiceFactory;
    protected EmailFactory emailFactory;

    public FillBaseTemplate() {
        employeeFactory = new EmployeeFactory();
        taskFactory = new TaskFactory();
        holidayFactory = new HolidayFactory();
        notificationFactory = new NotificationFactory();
        suggestionFactory = new SuggestionFactory();
        shopServiceFactory = new ShopServiceFactory();
        emailFactory = new EmailFactory();

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
    }

    protected void init() {
    }

    protected void setup() {
        ContractRequest contractRequest = new ContractRequest("123", 26, 50000);
        AdminRequest adminRequest = new AdminRequest("Szef", "Ceo", "szef.ceo@company.com",
                "haslo123", contractRequest);
        restTemplate.postForEntity("/setup-admin", adminRequest, String.class);
        restTemplate.postForEntity("/setup-teams", null, String.class);
        restTemplate.postForEntity("/setup-daily-plan", null, String.class);
        restTemplate.postForEntity("/setup-reports", null, String.class);
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

    protected void addOneHolidayRequest(long employeeId, boolean shouldPost) {
        holidayRequest = holidayFactory.generateHolidayRequest();
        holidayRequest.setDuration(1);
        if (shouldPost) {
            setupToken();
            restTemplate.postForEntity("/employees/{id}/holidays", new HttpEntity<>(holidayRequest,
                    requestHeaders), Holiday.class, employeeId);
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

    protected void addOneEmailEntityRequest(boolean shouldPost) {
        emailEntityRequest = emailFactory.generateRequestWithAddress();
        if (shouldPost) {
            restTemplate.postForEntity("/emails", new HttpEntity<>(emailEntityRequest, requestHeaders),
                    EmailEntity.class);
        }
    }
}

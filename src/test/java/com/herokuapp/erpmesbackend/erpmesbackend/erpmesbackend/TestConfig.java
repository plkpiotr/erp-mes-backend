package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.NotificationDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.SuggestionDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.factory.NotificationFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.factory.SuggestionFactory;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.NotificationRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.SuggestionRequest;
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
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.HolidayType;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Role;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.AdminRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.ContractRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.EmployeeRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.HolidayRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public abstract class TestConfig {

    protected static final long ADMIN_ID = 1;

    @Autowired
    protected TestRestTemplate restTemplate;

    protected HttpHeaders requestHeaders;

    private final EmployeeFactory employeeFactory;
    private final HolidayFactory holidayFactory;
    private final ShopServiceFactory shopServiceFactory;
    private final TaskFactory taskFactory;
    private final NotificationFactory notificationFactory;
    private final SuggestionFactory suggestionFactory;
    protected final List<EmployeeRequest> employeeRequests;
    protected final List<HolidayRequest> holidayRequests;
    protected final List<ItemRequest> itemRequests;
    protected final List<DeliveryRequest> deliveryRequests;
    protected final List<ShopServiceRequest> orderRequests;
    protected final List<ShopServiceRequest> returnRequests;
    protected final List<ShopServiceRequest> complaintRequests;
    protected final List<SpecialPlanRequest> specialPlanRequests;
    protected final List<TaskRequest> taskRequests;
    protected final List<NotificationRequest> notificationRequests;
    protected final List<SuggestionRequest> suggestionRequests;

    public TestConfig() {
        employeeFactory = new EmployeeFactory();
        holidayFactory = new HolidayFactory();
        shopServiceFactory = new ShopServiceFactory();
        taskFactory = new TaskFactory();
        notificationFactory = new NotificationFactory();
        suggestionFactory = new SuggestionFactory();
        employeeRequests = new ArrayList<>();
        holidayRequests = new ArrayList<>();
        itemRequests = new ArrayList<>();
        deliveryRequests = new ArrayList<>();
        orderRequests = new ArrayList<>();
        returnRequests = new ArrayList<>();
        complaintRequests = new ArrayList<>();
        specialPlanRequests = new ArrayList<>();
        taskRequests = new ArrayList<>();
        notificationRequests = new ArrayList<>();
        suggestionRequests = new ArrayList<>();
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

    protected void setupEmployees() {
        EmployeeDTO[] body = restTemplate.exchange("/employees",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class).getBody();
        if (employeeRequests.isEmpty() && body.length <= 1) {
            employeeRequests.add(employeeFactory.generateEmployeeRequestWithRole(Role.ADMIN_ACCOUNTANT));
            employeeRequests.add(employeeFactory.generateEmployeeRequestWithRole(Role.ADMIN_ANALYST));
            employeeRequests.add(employeeFactory.generateEmployeeRequestWithRole(Role.ADMIN_WAREHOUSE));
            employeeRequests.add(employeeFactory.generateEmployeeRequestWithRole(Role.ACCOUNTANT));
            employeeRequests.add(employeeFactory.generateEmployeeRequestWithRole(Role.ANALYST));
            employeeRequests.add(employeeFactory.generateEmployeeRequestWithRole(Role.WAREHOUSE));

            employeeRequests.forEach(request -> restTemplate.postForEntity("/employees",
                    new HttpEntity<>(request, requestHeaders), EmployeeDTO.class));
        }
    }

    protected EmployeeRequest getOneEmployeeRequest() {
        return employeeFactory.generateEmployeeRequestWithRole(Role.WAREHOUSE);
    }

    protected void setupHolidays() {
        Optional<EmployeeDTO> first = Arrays.asList(restTemplate.exchange("/employees",
                HttpMethod.GET, new HttpEntity<>(null, requestHeaders), EmployeeDTO[].class).getBody())
                .stream()
                .filter(employeeDTO -> employeeDTO.getRole().equals(Role.ADMIN_ACCOUNTANT))
                .findFirst();
        if (first.isPresent()) {
            Holiday[] body = restTemplate.exchange("/employees/{id}/holidays", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), Holiday[].class, first.get().getId()).getBody();
            if (holidayRequests.isEmpty() && body.length == 0) {
                Arrays.asList(HolidayType.values()).forEach(type -> holidayRequests.add(getOneHolidayRequest(type)));
                holidayRequests.forEach(request -> restTemplate.postForEntity("/employees/{id}/holidays",
                        new HttpEntity<>(request, requestHeaders), Holiday.class, first.get().getId()));
            }
        }
    }

    protected HolidayRequest getOneHolidayRequest(HolidayType type) {
        return holidayFactory.generateHolidayRequestWithType(type);
    }

    protected void setupItems() {
        Item[] body = restTemplate.exchange("/items", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Item[].class).getBody();
        if (itemRequests.isEmpty() && body.length == 0) {
            for (int i = 0; i < 10; i++) {
                itemRequests.add(new ItemRequest("Random name" + i+1, i+5, i+10));
            }
            itemRequests.forEach(request -> restTemplate.postForEntity("/items", new HttpEntity<>(request,
                    requestHeaders), Item.class));
        }
    }

    protected void setupDeliveries() {
        Delivery[] body = restTemplate.exchange("/deliveries", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Delivery[].class).getBody();
        if (deliveryRequests.isEmpty() && body.length == 0) {
            for (int i = 0; i < 10; i++) {
                List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
                deliveryItemRequests.add(new DeliveryItemRequest(i + 1, 100));
                deliveryRequests.add(new DeliveryRequest(deliveryItemRequests, LocalDate.now().plusDays(1)));
            }
            deliveryRequests.forEach(request -> restTemplate.postForEntity("/deliveries", new HttpEntity<>(request,
                    requestHeaders), Delivery.class));
        }
    }

    protected DeliveryRequest getOneDeliveryRequest() {
        List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
        deliveryItemRequests.add(new DeliveryItemRequest(1, 100));
        return new DeliveryRequest(deliveryItemRequests, LocalDate.now().plusDays(1));
    }

    protected void setupItemsAndSupply() {
        setupItems();
        List<Item> items = Arrays.asList(restTemplate.exchange("/items", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Item[].class).getBody());
        items.forEach(item -> restTemplate.postForEntity("/items/{id}/supply",
                new HttpEntity<>(10, requestHeaders), Item.class, item.getId()));
    }

    protected void setupOrders() {
        Order[] body = restTemplate.exchange("/orders", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Order[].class).getBody();
        if (orderRequests.isEmpty() && body.length == 0) {
            for (int i = 0; i < 10; i++) {
                orderRequests.add(getOneShopServiceRequestWithItemId(i+1));
            }
            orderRequests.forEach(request -> restTemplate.postForEntity("/orders",
                    new HttpEntity<>(request, requestHeaders), Order.class));
        }
    }

    protected void setupReturns() {
        Return[] body = restTemplate.exchange("/returns", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Return[].class).getBody();
        if (returnRequests.isEmpty() && body.length == 0) {
            for (int i = 0; i < 10; i++) {
                returnRequests.add(getOneShopServiceRequestWithItemId(i+1));
            }
            returnRequests.forEach(request -> restTemplate.postForEntity("/returns",
                    new HttpEntity<>(request, requestHeaders), Return.class));
        }
    }

    protected void setupComplaints() {
        Complaint[] body = restTemplate.exchange("/complaints", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Complaint[].class).getBody();
        if (complaintRequests.isEmpty() && body.length == 0) {
            for (int i = 0; i < 10; i++) {
                complaintRequests.add(getOneComplaintRequestWithItemId(i+1));
            }
            complaintRequests.forEach(request -> restTemplate.postForEntity("/complaints",
                    new HttpEntity<>(request, requestHeaders), Complaint.class));
        }
    }

    protected ShopServiceRequest getOneShopServiceRequestWithItemId(long id) {
        List<DeliveryItemRequest> deliveryItemRequests = new ArrayList<>();
        deliveryItemRequests.add(new DeliveryItemRequest(id, 1));
        String firstName = shopServiceFactory.generateFirstName();
        String lastName = shopServiceFactory.generateLastName();
        String email = shopServiceFactory.generateEmail();
        String phoneNumber = shopServiceFactory.generatePhoneNumber();
        String street = shopServiceFactory.generateStreet();
        String houseNumber = shopServiceFactory.generateHouseNumber();
        String city = shopServiceFactory.generateCity();
        String postalCode = shopServiceFactory.generatePostalCode();
        return new ShopServiceRequest(firstName, lastName, email, phoneNumber, street, houseNumber,
                city, postalCode, deliveryItemRequests, LocalDate.now().plusDays(1), null,
                null);
    }

    protected ShopServiceRequest getOneComplaintRequestWithItemId(long id) {
        ShopServiceRequest complaintRequest = getOneShopServiceRequestWithItemId(id);
        complaintRequest.setRequestedResolution(Resolution.MONEY_RETURN);
        complaintRequest.setFault("Failty item");
        return complaintRequest;
    }

    protected SpecialPlanRequest getOneSpecialPlanRequest() {
        Random r = new Random();
        return new SpecialPlanRequest("Special plan", LocalDate.now().plusDays(r.nextInt(30)),
                r.nextInt(30), r.nextInt(30), r.nextInt(20), r.nextInt(20));
    }

    protected void setupSpecialPlans() {
        SpecialPlan[] body = restTemplate.exchange("/special-plans", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), SpecialPlan[].class).getBody();
        if (specialPlanRequests.isEmpty() && body.length == 0) {
            for (int i = 0; i < 10; i++) {
                specialPlanRequests.add(getOneSpecialPlanRequest());
            }
            specialPlanRequests.forEach(request -> restTemplate.postForEntity("/special-plans",
                    new HttpEntity<>(request, requestHeaders), SpecialPlan.class));
        }
    }

    protected TaskRequest getOneTaskRequestWithAssigneeId(long id) {
        String name = taskFactory.generateName();
        List<Long> precedingTasksIds = new ArrayList<>();
        Integer estimatedTime = taskFactory.generateEstimatedTime();
        LocalDateTime deadline = taskFactory.generateDeadline();
        String details = taskFactory.generateDetails();

        return new TaskRequest(name, precedingTasksIds, id, estimatedTime, deadline,
                null, details, Type.OTHER);
    }

    protected void setupTasks() {
        setupEmployees();
        TaskDTO[] body = restTemplate.exchange("/tasks", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), TaskDTO[].class).getBody();
        if (taskRequests.isEmpty() && body.length == 0) {
            for (int i = 0; i < 6; i++) {
                taskRequests.add(getOneTaskRequestWithAssigneeId(i + 1));
            }
            taskRequests.forEach(request -> restTemplate.postForEntity("/tasks",
                    new HttpEntity<>(request, requestHeaders), TaskDTO.class));
        }
    }

    protected NotificationRequest getOneNotificationRequestWithIds(Long... ids) {
        String instruction = notificationFactory.generateInstruction();
        String description = notificationFactory.generateDescription();
        List<Long> consigneeIds = new ArrayList<>(Arrays.asList(ids));
        Type type = Type.ORDER;

        return new NotificationRequest(instruction, description, consigneeIds, type);
    }

    protected void setupNotifications() {
        setupEmployees();
        NotificationDTO[] body = restTemplate.exchange("/notifications", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), NotificationDTO[].class).getBody();
        if (notificationRequests.isEmpty() && body.length == 0) {
            for (int i = 0; i < 5; i++) {
                Long[] ids = {(long) (i + 1), (long) (i + 2)};
                notificationRequests.add(getOneNotificationRequestWithIds(ids));
            }
            notificationRequests.forEach(request -> restTemplate.postForEntity("/notifications",
                    new HttpEntity<>(request, requestHeaders), NotificationDTO.class));
        }
    }

    protected SuggestionRequest getOneSuggestionRequestWithIds(Long... ids) {
        String name = suggestionFactory.generateName();
        String description = suggestionFactory.generateDescription();
        List<Long> recipientIds = new ArrayList<>(Arrays.asList(ids));

        return new SuggestionRequest(name, description, recipientIds);
    }

    protected void setupSuggestions() {
        setupEmployees();
        SuggestionDTO[] body = restTemplate.exchange("/suggestions", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), SuggestionDTO[].class).getBody();
        if (suggestionRequests.isEmpty() && body.length == 0) {
            for (int i = 0; i < 5; i++) {
                Long[] ids = {(long) (i + 1), (long) (i + 2)};
                suggestionRequests.add(getOneSuggestionRequestWithIds(ids));
            }
            suggestionRequests.forEach(request -> restTemplate.postForEntity("/suggestions",
                    new HttpEntity<>(request, requestHeaders), SuggestionDTO.class));
        }
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.notifications;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.orders.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.orders.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public NotificationController(NotificationRepository notificationRepository, EmployeeRepository employeeRepository,
                                  OrderRepository orderRepository) {
        this.notificationRepository = notificationRepository;
        this.employeeRepository = employeeRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/nofications")
    @ResponseStatus(HttpStatus.OK)
    public List<Notification> getAllNotifications() {
        return new ArrayList<>(notificationRepository.findAll());
    }

    @GetMapping("/nofications/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Notification getOneNotification(@PathVariable("id") Long id) {
        checkIfNotificationExists(id);
        return notificationRepository.findById(id).get();
    }

    @GetMapping("/employees/{id}/notifications")
    @ResponseStatus(HttpStatus.OK)
    public List<Notification> getNotificationsByConsignee(@PathVariable("id") Long id) {
        return notificationRepository.findByConsigneesContaining(id).isPresent() ?
                notificationRepository.findByConsigneesContaining(id).get() : new ArrayList<>();
    }

    @PostMapping("/notifications")
    @ResponseStatus(HttpStatus.CREATED)
    public Notification addOneNotification(@RequestBody NotificationRequest notificationRequest) {
        State state = notificationRequest.getState();
        String instruction = notificationRequest.getInstruction();
        String description = notificationRequest.getDescription();

        Order order = null;
        if (notificationRequest.getOrderId() != null) {
            checkIfOrderExists(notificationRequest.getOrderId());
            order = orderRepository.findById(notificationRequest.getOrderId()).get();
        }

        Employee notifier = null;
        if (notificationRequest.getNotifierId() != null) {
            checkIfNotifierExists(notificationRequest.getNotifierId());
            notifier = employeeRepository.findById(notificationRequest.getNotifierId()).get();
        }

        List<Employee> consignees = new ArrayList<>();
        notificationRequest.getConsigneeIds().forEach(this::checkIfConsigneeExists);
        notificationRequest.getConsigneeIds().forEach(id -> consignees.add(employeeRepository.findById(id).get()));

        Notification notification = new Notification(state, instruction, description, order, notifier, consignees);
        notificationRepository.save(notification);
        return notification;
    }

    @PutMapping("notifications/{id}")
    public HttpStatus updateNotification(@PathVariable("id") Long id,
                                         @RequestBody NotificationRequest notificationRequest) {
        checkIfNotifierExists(id);
        Notification notification = notificationRepository.findById(id).get();

        notification.setState(notificationRequest.getState());
        notification.setInstruction(notificationRequest.getInstruction());
        notification.setDescription(notificationRequest.getDescription());

        if (notificationRequest.getNotifierId() != null) {
            checkIfTransfereeExists(notificationRequest.getTransfereeId());
            notification.setTransferee(employeeRepository.findById(notificationRequest.getNotifierId()).get());
        }

        notificationRepository.save(notification);
        return HttpStatus.NO_CONTENT;
    }

    @DeleteMapping("/notifications/{id}")
    public HttpStatus removeNotification(@PathVariable("id") Long id) {
        checkIfNotifierExists(id);
        notificationRepository.delete(notificationRepository.findById(id).get());
        return HttpStatus.OK;
    }

    private void checkIfNotificationExists(Long id) {
        if (!notificationRepository.findById(id).isPresent())
            throw new NotFoundException("Such notification doesn't exist!");
    }

    private void checkIfOrderExists(Long id) {
        if (!orderRepository.findById(id).isPresent())
            throw new NotFoundException("Chosen order doesn't exist!");
    }

    private void checkIfNotifierExists(Long id) {
        if (!employeeRepository.findById(id).isPresent())
            throw new NotFoundException("Chosen notifier doesn't exist!");
    }

    private void checkIfTransfereeExists(Long id) {
        if (!employeeRepository.findById(id).isPresent())
            throw new NotFoundException("Chosen transferee doesn't exist!");
    }

    private void checkIfConsigneeExists(Long id) {
        if (!employeeRepository.findById(id).isPresent())
            throw new NotFoundException("At least one of the chosen consignees doesn't exist!");
    }
}

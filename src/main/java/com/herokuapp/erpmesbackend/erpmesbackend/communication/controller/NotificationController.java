package com.herokuapp.erpmesbackend.erpmesbackend.communication.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.NotificationDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.repository.NotificationRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.NotificationRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Notification;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.State;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.OrderRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
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

    @GetMapping("/notifications")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDTO> getAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        notifications.forEach(notification -> notificationDTOS.add(new NotificationDTO(notification)));
        return notificationDTOS;
    }

    @GetMapping("/notifications/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationDTO getOneNotification(@PathVariable("id") Long id) {
        checkIfNotificationExists(id);
        return new NotificationDTO(notificationRepository.findById(id).get());
    }

    @GetMapping("/employees/{id}/notifications")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDTO> getNotificationsByConsignee(@PathVariable("id") Long id) {
        checkIfConsigneeExists(id);
        if (!notificationRepository.findByConsigneesId(id).isPresent()) {
            return new ArrayList<>();
        }
        List<Notification> notifications = notificationRepository.findByConsigneesId(id).get();
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        notifications.forEach(notification -> notificationDTOS.add(new NotificationDTO(notification)));
        return notificationDTOS;
    }

    @PostMapping("/notifications")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationDTO addOneNotification(@RequestBody NotificationRequest notificationRequest) {
        String instruction = notificationRequest.getInstruction();

        String description = null;
        if (notificationRequest.getDescription() != null)
            description = notificationRequest.getDescription();

        Employee notifier = null;
        if (notificationRequest.getNotifierId() != null) {
            checkIfNotifierExists(notificationRequest.getNotifierId());
            notifier = employeeRepository.findById(notificationRequest.getNotifierId()).get();
        }

        List<Employee> consignees = new ArrayList<>();
        notificationRequest.getConsigneeIds().forEach(this::checkIfConsigneeExists);
        notificationRequest.getConsigneeIds().forEach(id -> consignees.add(employeeRepository.findById(id).get()));

        Type type = null;
        if (notificationRequest.getType() != null)
            type = notificationRequest.getType();

        Long reference = null;
        if (notificationRequest.getReference() != null)
            reference = notificationRequest.getReference();

        Notification notification = new Notification(instruction, description, notifier, consignees, type, reference);

        notificationRepository.save(notification);
        return new NotificationDTO(notification);
    }

    @PutMapping("notifications/{id}")
    public HttpStatus updateTransfereeNotification(@PathVariable("id") Long id, @RequestBody Long transfereeId) {
        checkIfNotifierExists(id);
        Notification notification = notificationRepository.findById(id).get();

        if (transfereeId != null) {
            checkIfTransfereeExists(transfereeId);
            notification.setTransferee(employeeRepository.findById(transfereeId).get());
        }

        notificationRepository.save(notification);
        return HttpStatus.NO_CONTENT;
    }

    @PatchMapping("notification/{id}")
    public HttpStatus updateStateNotification(@PathVariable("id") Long id, @RequestBody State state) {
        checkIfNotifierExists(id);
        Notification notification = notificationRepository.findById(id).get();

        notification.setState(state);

        notificationRepository.save(notification);
        return HttpStatus.NO_CONTENT;
    }

    private void checkIfNotificationExists(Long id) {
        if (!notificationRepository.findById(id).isPresent())
            throw new NotFoundException("Such notification doesn't exist!");
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

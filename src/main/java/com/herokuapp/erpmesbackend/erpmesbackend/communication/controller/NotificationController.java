package com.herokuapp.erpmesbackend.erpmesbackend.communication.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.NotificationDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Notification;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.State;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.repository.NotificationRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.NotificationRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.OrderRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Employee notifier = employeeRepository.findByEmail(username).get();
        List<Notification> notifications = new ArrayList<>();
        List<Notification> ownNotifications = new ArrayList<>();

        if (notifier.isManager()) {
            notifications = notificationRepository.findAll();
        } else {
            if (notificationRepository.findByConsigneesId(notifier.getId()).isPresent()) {
                notifications = notificationRepository.findByConsigneesId(notifier.getId()).get();
            }
            if (notificationRepository.findByNotifierId(notifier.getId()).isPresent()) {
                ownNotifications = notificationRepository.findByNotifierId(notifier.getId()).get();
                notifications.addAll(ownNotifications);
                notifications = notifications.stream().distinct().collect(Collectors.toList());
            }
        }

        List<NotificationDTO> notificationDTOs = new ArrayList<>();
        notifications.forEach(notification -> notificationDTOs.add(new NotificationDTO(notification)));
        return notificationDTOs;
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
        if (notificationRequest.getDescription() != null) {
            description = notificationRequest.getDescription();
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Employee notifier = employeeRepository.findByEmail(username).get();

        List<Employee> consignees = new ArrayList<>();
        notificationRequest.getConsigneeIds().forEach(this::checkIfConsigneeExists);
        notificationRequest.getConsigneeIds().forEach(id -> consignees.add(employeeRepository.findById(id).get()));

        Type type = notificationRequest.getType();

        Notification notification = new Notification(instruction, description, notifier, consignees, type);

        notificationRepository.save(notification);
        return new NotificationDTO(notification);
    }

    @PutMapping("notifications/{id}")
    public Notification setNextState(@PathVariable("id") Long id) {
        checkIfNotifierExists(id);
        Notification notification = notificationRepository.findById(id).get();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Employee employee = employeeRepository.findByEmail(username).get();

        if (notification.getState() == State.REPORTED) {
            notification.setState(State.IN_PROGRESS);
            notification.setStartTime(LocalDateTime.now());
            notification.setTransferee(employee);
        } else if (notification.getState() == State.IN_PROGRESS) {
            notification.setState(State.RESOLVED);
            notification.setEndTime(LocalDateTime.now());
            notification.setEndEmployee(employee);

        }

        notificationRepository.save(notification);
        return notification;
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
        if (!notificationRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such notification doesn't exist!");
        }
    }

    private void checkIfNotifierExists(Long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Chosen notifier doesn't exist!");
        }
    }

    private void checkIfTransfereeExists(Long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("Chosen transferee doesn't exist!");
        }
    }

    private void checkIfConsigneeExists(Long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("At least one of the chosen consignees doesn't exist!");
        }
    }
}

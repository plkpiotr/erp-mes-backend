package com.herokuapp.erpmesbackend.erpmesbackend.communication.service;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.NotificationDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Notification;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.State;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.repository.NotificationRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.NotificationRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, EmployeeRepository employeeRepository) {
        this.notificationRepository = notificationRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<NotificationDTO> getAllNotifications() {
        Employee notifier = employeeRepository.findByEmail(getEmailLoggedInUser()).get();
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

    public NotificationDTO getOneNotification(Long id) {
        checkIfNotificationExists(id);
        return new NotificationDTO(notificationRepository.findById(id).get());
    }

    public List<NotificationDTO> getNotificationsByConsignee(Long id) {
        checkIfConsigneeExists(id);
        if (!notificationRepository.findByConsigneesId(id).isPresent()) {
            return new ArrayList<>();
        }
        List<Notification> notifications = notificationRepository.findByConsigneesId(id).get();
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        notifications.forEach(notification -> notificationDTOS.add(new NotificationDTO(notification)));
        return notificationDTOS;
    }

    public NotificationDTO addOneNotification(NotificationRequest notificationRequest) {
        String instruction = notificationRequest.getInstruction();

        String description = null;
        if (notificationRequest.getDescription() != null) {
            description = notificationRequest.getDescription();
        }

        Employee notifier = employeeRepository.findByEmail(getEmailLoggedInUser()).get();

        List<Employee> consignees = new ArrayList<>();
        notificationRequest.getConsigneeIds().forEach(this::checkIfConsigneeExists);
        notificationRequest.getConsigneeIds().forEach(id -> consignees.add(employeeRepository.findById(id).get()));

        Type type = notificationRequest.getType();

        Notification notification = new Notification(instruction, description, notifier, consignees, type);

        notificationRepository.save(notification);
        return new NotificationDTO(notification);
    }

    public NotificationDTO setNextState(Long id) {
        checkIfNotifierExists(id);
        Notification notification = notificationRepository.findById(id).get();

        Employee employee = employeeRepository.findByEmail(getEmailLoggedInUser()).get();

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
        return new NotificationDTO(notification);
    }

    private String getEmailLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails) principal).getUsername();
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

    private void checkIfConsigneeExists(Long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("At least one of the chosen consignees doesn't exist!");
        }
    }
}

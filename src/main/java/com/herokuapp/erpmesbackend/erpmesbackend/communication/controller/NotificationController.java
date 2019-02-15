package com.herokuapp.erpmesbackend.erpmesbackend.communication.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.NotificationDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.NotificationRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDTO> getAllNotifications() {
        return notificationService.getAllNotifications();
    }

    @GetMapping("/notifications/{id}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationDTO getOneNotification(@PathVariable("id") Long id) {
        return notificationService.getOneNotification(id);
    }

    @GetMapping("/employees/{id}/notifications")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDTO> getNotificationsByConsignee(@PathVariable("id") Long id) {
        return notificationService.getNotificationsByConsignee(id);
    }

    @PostMapping("/notifications")
    @ResponseStatus(HttpStatus.CREATED)
    public NotificationDTO addOneNotification(@RequestBody NotificationRequest notificationRequest) {
        return notificationService.addOneNotification(notificationRequest);
    }

    @PutMapping("notifications/{id}")
    public NotificationDTO setNextState(@PathVariable("id") Long id) {
        return notificationService.setNextState(id);
    }
}

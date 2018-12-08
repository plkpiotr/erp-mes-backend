package com.herokuapp.erpmesbackend.erpmesbackend.communication.dto;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Notification;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.State;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class NotificationDTO {

    private long id;
    private State state;
    private String instruction;
    private String description;
    private EmployeeDTO notifier;
    private EmployeeDTO transferee;
    private List<EmployeeDTO> consignees;
    private LocalDateTime creationTime;
    private Type type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EmployeeDTO endEmployee;

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.state = notification.getState();
        this.instruction = notification.getInstruction();
        this.description = notification.getDescription();
        this.notifier = new EmployeeDTO(notification.getNotifier());
        this.consignees = new ArrayList<>();
        notification.getConsignees().forEach(consignee -> this.consignees.add(new EmployeeDTO(consignee)));
        this.creationTime = notification.getCreationTime();
        this.type = notification.getType();

        if (notification.getStartTime() != null) {
            this.startTime = notification.getStartTime();
            this.transferee = new EmployeeDTO(notification.getTransferee());
        }

        if (notification.getEndTime() != null) {
            this.endTime = notification.getEndTime();
            this.endEmployee = new EmployeeDTO(notification.getEndEmployee());
        }
    }

    public NotificationDTO(String instruction, String description, EmployeeDTO notifier, List<EmployeeDTO> consignees,
                           Type type) {
        this.instruction = instruction;
        this.description = description;
        this.notifier = notifier;
        this.consignees = consignees;
        this.type = type;
    }
}

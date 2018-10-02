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
    private Long reference;

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.state = notification.getState();
        this.instruction = notification.getInstruction();
        this.description = notification.getDescription();
        this.notifier = new EmployeeDTO(notification.getNotifier());
        if (notification.getTransferee() != null) {
            this.transferee = new EmployeeDTO(notification.getTransferee());
        }
        this.consignees = new ArrayList<>();
        notification.getConsignees().forEach(consignee -> this.consignees
                .add(new EmployeeDTO(consignee)));
        this.creationTime = notification.getCreationTime();
        this.type = notification.getType();
        this.reference = notification.getReference();
    }

    public NotificationDTO(String instruction, String description, EmployeeDTO notifier,
                           List<EmployeeDTO> consignees, Type type, Long reference) {
        this.instruction = instruction;
        this.description = description;
        this.notifier = notifier;
        this.consignees = consignees;
        this.type = type;
        this.reference = reference;
    }

    public boolean checkIfDataEquals(NotificationDTO notification) {
        return instruction.equals(notification.getInstruction()) &&
                description.equals(notification.getDescription()) &&
                notifier.checkIfDataEquals(notification.getNotifier()) &&
                compareConsignees(notification.getConsignees());
    }

    private boolean compareConsignees(List<EmployeeDTO> consigneeList) {
        for (EmployeeDTO employee : consignees) {
            if (consigneeList.stream().noneMatch(t -> t.checkIfDataEquals(employee))) {
                return false;
            }
        }
        return true;
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.notifications;

import com.herokuapp.erpmesbackend.erpmesbackend.orders.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {

    @NonNull
    private State state;

    @NonNull
    private String instruction;

    private String description;
    private long orderId;

    @NonNull
    private long notifierId;

    @NonNull
    private List<Long> consigneeIds;
}

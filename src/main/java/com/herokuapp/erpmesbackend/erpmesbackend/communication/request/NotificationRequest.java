package com.herokuapp.erpmesbackend.erpmesbackend.communication.request;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;
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
    private String instruction;

    private String description;

    @NonNull
    private List<Long> consigneeIds;

    @NonNull
    private Type type;
}

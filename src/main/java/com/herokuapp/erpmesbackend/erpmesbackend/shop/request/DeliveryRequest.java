package com.herokuapp.erpmesbackend.erpmesbackend.shop.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryRequest {

    @NotNull
    private List<DeliveryItemRequest> deliveryItemRequests;

    @NotNull
    private LocalDate scheduledFor;
}

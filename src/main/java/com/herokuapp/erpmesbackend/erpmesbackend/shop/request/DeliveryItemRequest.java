package com.herokuapp.erpmesbackend.erpmesbackend.shop.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryItemRequest {

    @NotNull
    private long itemId;

    @NotNull
    private int quantity;
}

package com.herokuapp.erpmesbackend.erpmesbackend.shop.orders;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints.Resolution;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryItemRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopServiceRequest {

    @NonNull
    private String firstName;

    @NonNull
    private String lastName;

    @NonNull
    private String email;

    private String phoneNumber;

    @NonNull
    private String street;

    @NonNull
    private String houseNumber;

    @NonNull
    private String city;

    @NonNull
    private String postalCode;

    @NonNull
    private List<DeliveryItemRequest> deliveryItemRequests;

    @NonNull
    private LocalDate scheduledFor;

    private Resolution RequestedResolution;
}

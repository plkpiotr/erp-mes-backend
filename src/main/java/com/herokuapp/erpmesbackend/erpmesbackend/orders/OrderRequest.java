package com.herokuapp.erpmesbackend.erpmesbackend.orders;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NonNull
    private Status status;

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
    private List<Item> itemIds;
}

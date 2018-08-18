package com.herokuapp.erpmesbackend.erpmesbackend.warehouses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseRequest {

    @NonNull
    private String street;

    @NonNull
    private String houseNumber;

    @NonNull
    private String city;

    @NonNull
    private String postalCode;

    @NonNull
    private List<Long> itemIds;
}

package com.herokuapp.erpmesbackend.erpmesbackend.shop.request;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequest {

    @NotNull
    private String name;

    @NotNull
    private double stockPrice;

    @NotNull
    private double price;

    public Item extractItem() {
        return new Item(name, 0, stockPrice, price);
    }
}

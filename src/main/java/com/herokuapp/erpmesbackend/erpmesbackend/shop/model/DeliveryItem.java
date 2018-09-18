package com.herokuapp.erpmesbackend.erpmesbackend.shop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class DeliveryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Item item;

    private int quantity;

    public DeliveryItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public boolean checkIfDataEquals(DeliveryItem deliveryItem) {
        return item.checkIfDataEquals(deliveryItem.getItem()) &&
                quantity == deliveryItem.getQuantity();
    }
}

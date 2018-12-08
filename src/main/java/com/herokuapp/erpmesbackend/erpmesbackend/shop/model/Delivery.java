package com.herokuapp.erpmesbackend.erpmesbackend.shop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany
    List<DeliveryItem> deliveryItems;

    private LocalDate scheduledFor;
    private double value;
    private boolean confirmed;

    public Delivery(List<DeliveryItem> deliveryItems, LocalDate scheduledFor) {
        this.deliveryItems = deliveryItems;
        this.scheduledFor = scheduledFor;
        this.value = deliveryItems.stream()
                .map(deliveryItem -> deliveryItem.getItem().getStockPrice()*deliveryItem.getQuantity())
                .mapToDouble(Double::doubleValue)
                .sum();
        this.confirmed = false;
    }

    public void confirm() {
        this.confirmed = true;
    }
}

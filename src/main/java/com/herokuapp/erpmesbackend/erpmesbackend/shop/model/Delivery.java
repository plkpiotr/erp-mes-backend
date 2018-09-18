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

    public Delivery(List<DeliveryItem> deliveryItems, LocalDate scheduledFor) {
        this.deliveryItems = deliveryItems;
        this.scheduledFor = scheduledFor;
        this.value = deliveryItems.stream()
                .map(deliveryItem -> deliveryItem.getItem().getStockPrice()*deliveryItem.getQuantity())
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    public boolean checkIfDataEquals(Delivery delivery) {
        return scheduledFor.equals(delivery.getScheduledFor()) &&
                value == delivery.getValue() &&
                compareItems(delivery.getDeliveryItems());
    }

    private boolean compareItems(List<DeliveryItem> deliveryItemsToCompare) {
        for (DeliveryItem item : deliveryItemsToCompare) {
            if (deliveryItems.stream()
                    .noneMatch(deliveryItem -> deliveryItem.checkIfDataEquals(item))) {
                return false;
            }
        }
        return true;
    }
}

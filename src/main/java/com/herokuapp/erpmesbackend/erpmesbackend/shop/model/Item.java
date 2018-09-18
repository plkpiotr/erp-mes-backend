package com.herokuapp.erpmesbackend.erpmesbackend.shop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private int quantity;
    private double stockPrice;
    private double originalPrice;

    @Setter
    private double currentPrice;

    public Item(String name, int quantity, double stockPrice, double price) {
        this.name = name;
        this.quantity = quantity;
        this.stockPrice = stockPrice;
        this.originalPrice = price;
        this.currentPrice = price;
    }

    public void supply(int q) {
        quantity += q;
    }

    public void sell(int q) {
        quantity -= q;
    }

    public boolean checkIfDataEquals(Item item) {
        return name.equals(item.getName()) &&
                stockPrice == item.getStockPrice() &&
                originalPrice == item.getOriginalPrice() &&
                currentPrice == item.getCurrentPrice();
    }
}

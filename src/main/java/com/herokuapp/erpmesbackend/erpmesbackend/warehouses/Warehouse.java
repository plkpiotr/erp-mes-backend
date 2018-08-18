package com.herokuapp.erpmesbackend.erpmesbackend.warehouses;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String houseNumber;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    @Pattern(regexp = "[0-9]{2}-?[0-9]{3}")
    private String postalCode;

    @OneToMany
    private List<Item> items;

    public Warehouse(String street, String houseNumber, String city, String postalCode, String country,
                     List<Item> items) {
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
        this.postalCode = postalCode;
        this.items = items;
    }

    public boolean checkIfDataEquals(Warehouse warehouse) {
        return street.equals(warehouse.getStreet()) &&
                houseNumber.equals(warehouse.getHouseNumber()) &&
                city.equals(warehouse.getCity()) &&
                postalCode.equals(warehouse.getPostalCode()) &&
                compareItems(warehouse.getItems());
    }

    private boolean compareItems(List<Item> itemsList) {
        if (itemsList.isEmpty())
            return true;
        for (Item item: items) {
            if (itemsList.stream().noneMatch(i -> i.checkIfDataEquals(item)))
                return false;
        }
        return true;
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.orders;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    @Email
    private String email;

    @Pattern(regexp = "[0-9]{9}")
    private String phoneNumber;

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

    public Order(Status status, String firstName, String lastName, String email, String phoneNumber, String street,
                 String houseNumber, String city, String postalCode, List<Item> items) {
        this.status = status;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
        this.postalCode = postalCode;
        this.items = items;
    }

    public boolean checkIfDataEquals(Order order) {
        return status.equals(order.getStatus()) &&
                firstName.equals(order.getLastName()) &&
                lastName.equals(order.getLastName()) &&
                email.equals(order.getEmail()) &&
                phoneNumber.equals(order.getPhoneNumber()) &&
                street.equals(order.getStreet()) &&
                houseNumber.equals(order.getHouseNumber()) &&
                city.equals(order.getCity()) &&
                postalCode.equals(order.getPostalCode()) &&
                compareItems(order.getItems());
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

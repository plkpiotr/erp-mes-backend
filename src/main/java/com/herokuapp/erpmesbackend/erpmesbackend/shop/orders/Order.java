package com.herokuapp.erpmesbackend.erpmesbackend.shop.orders;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
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

    @Column(nullable = false)
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

    @Column(nullable = false)
    @OneToMany
    private List<DeliveryItem> deliveryItems;

    @Column(nullable = false)
    private LocalDate scheduledFor;
    private LocalDate submissionDate;
    private LocalDate closingDate;

    @Column(nullable = false)
    private Double value;

    public Order(String firstName, String lastName, String email, String phoneNumber, String street,
                 String houseNumber, String city, String postalCode, List<DeliveryItem> deliveryItems,
                 LocalDate scheduledFor) {
        this.status = Status.WAITING_FOR_PAYMENT;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
        this.postalCode = postalCode;
        this.deliveryItems = deliveryItems;
        this.scheduledFor = scheduledFor;
        this.value = deliveryItems.stream()
            .map(deliveryItem -> deliveryItem.getItem().getCurrentPrice() * deliveryItem.getQuantity())
            .mapToDouble(Double::doubleValue)
            .sum();
        this.submissionDate = LocalDate.now();
    }

    public boolean checkIfDataEquals(Order order) {
        return status.equals(order.getStatus()) &&
                firstName.equals(order.getFirstName()) &&
                lastName.equals(order.getLastName()) &&
                email.equals(order.getEmail()) &&
                phoneNumber.equals(order.getPhoneNumber()) &&
                street.equals(order.getStreet()) &&
                houseNumber.equals(order.getHouseNumber()) &&
                city.equals(order.getCity()) &&
                postalCode.equals(order.getPostalCode()) &&
                compareDeliveryItems(order.getDeliveryItems()) &&
                scheduledFor.isEqual(order.getScheduledFor()) &&
                value.equals(order.getValue()) &&
                submissionDate.equals(order.getSubmissionDate());
    }

    private boolean compareDeliveryItems(List<DeliveryItem> deliveryItemList) {
        if (deliveryItemList.isEmpty())
            return true;
        for (DeliveryItem deliveryItem: deliveryItems) {
            if (deliveryItemList.stream().noneMatch(i -> i.checkIfDataEquals(deliveryItem)))
                return false;
        }
        return true;
    }
}

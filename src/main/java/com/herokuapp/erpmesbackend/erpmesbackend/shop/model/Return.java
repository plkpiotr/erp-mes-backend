package com.herokuapp.erpmesbackend.erpmesbackend.shop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Return {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private ReturnStatus status;

    private String firstName;
    private String lastName;

    @Email
    private String email;

    private String phoneNumber;
    private String street;
    private String houseNumber;
    private String city;
    private String postalCode;

    @OneToMany
    private List<DeliveryItem> deliveryItems;

    private LocalDate scheduledFor;
    private Double value;

    public Return(String firstName, String lastName, String email, String phoneNumber, String street,
                  String houseNumber, String city, String postalCode, List<DeliveryItem> deliveryItems,
                  LocalDate scheduledFor) {
        this.status = ReturnStatus.IN_PROGRESS;
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
    }

    public void updateStatus(ReturnStatus status) {
        this.status = status;
    }

    public boolean checkIfDataEquals(Return r) {
        return status.equals(r.getStatus()) &&
                firstName.equals(r.getFirstName()) &&
                lastName.equals(r.getLastName()) &&
                email.equals(r.getEmail()) &&
                phoneNumber.equals(r.getPhoneNumber()) &&
                street.equals(r.getStreet()) &&
                houseNumber.equals(r.getHouseNumber()) &&
                city.equals(r.getCity()) &&
                postalCode.equals(r.getPostalCode()) &&
                compareDeliveryItems(r.getDeliveryItems()) &&
                scheduledFor.isEqual(r.getScheduledFor()) &&
                value.equals(r.getValue());
    }

    private boolean compareDeliveryItems(List<DeliveryItem> deliveryItemList) {
        if (deliveryItemList.isEmpty())
            return true;
        for (DeliveryItem deliveryItem : deliveryItems) {
            if (deliveryItemList.stream().noneMatch(i -> i.checkIfDataEquals(deliveryItem)))
                return false;
        }
        return true;
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    @Enumerated(EnumType.STRING)
    private Resolution requestedResolution;

    @Enumerated(EnumType.STRING)
    private Resolution resolution;

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

    public Complaint(Resolution requestedResolution, String firstName, String lastName, String email, String phoneNumber, String street,
                     String houseNumber, String city, String postalCode, List<DeliveryItem> deliveryItems,
                     LocalDate scheduledFor) {
        this.status = ComplaintStatus.IN_PROGRESS;
        this.resolution = Resolution.UNRESOLVED;
        this.requestedResolution = requestedResolution;
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

    public void updateStatus(ComplaintStatus status) {
        this.status = status;
    }

    public void updateResolution(Resolution resolution) {
        this.resolution = resolution;
    }

    public boolean checkIfDataEquals(Complaint complaint) {
        return status.equals(complaint.getStatus()) &&
                requestedResolution.equals(complaint.getRequestedResolution()) &&
                resolution.equals(complaint.getResolution()) &&
                firstName.equals(complaint.getFirstName()) &&
                lastName.equals(complaint.getLastName()) &&
                email.equals(complaint.getEmail()) &&
                phoneNumber.equals(complaint.getPhoneNumber()) &&
                street.equals(complaint.getStreet()) &&
                houseNumber.equals(complaint.getHouseNumber()) &&
                city.equals(complaint.getCity()) &&
                postalCode.equals(complaint.getPostalCode()) &&
                compareDeliveryItems(complaint.getDeliveryItems()) &&
                scheduledFor.isEqual(complaint.getScheduledFor()) &&
                value.equals(complaint.getValue());
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

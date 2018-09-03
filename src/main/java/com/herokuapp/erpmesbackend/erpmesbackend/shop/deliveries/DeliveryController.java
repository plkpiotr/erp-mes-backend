package com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class DeliveryController {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping("/deliveries")
    @ResponseStatus(HttpStatus.OK)
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @PostMapping("/deliveries")
    @ResponseStatus(HttpStatus.CREATED)
    public Delivery addNewDelivery(@RequestBody DeliveryRequest request) {
        return deliveryService.addDelivery(request);
    }

    @GetMapping("/deliveries/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Delivery getOneDelivery(@PathVariable("id") long id) {
        deliveryService.checkIfDeliveryExists(id);
        return deliveryRepository.findById(id).get();
    }

    @GetMapping("/deliveries/recommended-delivery")
    public List<DeliveryItemRequest> recommendDelivery() {
        return deliveryService.getRecommendedDelivery();
    }
}

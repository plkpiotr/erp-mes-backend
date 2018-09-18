package com.herokuapp.erpmesbackend.erpmesbackend.shop.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Delivery;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.DeliveryRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.DeliveryItemRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.DeliveryRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class DeliveryController {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryRepository deliveryRepository, DeliveryService deliveryService) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryService = deliveryService;
    }

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

package com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.Item;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
public class DeliveryController {

    @Autowired
    private DeliveryItemRepository deliveryItemRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/deliveries")
    @ResponseStatus(HttpStatus.OK)
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    @PostMapping("/deliveries")
    @ResponseStatus(HttpStatus.CREATED)
    public Delivery addNewDelivery(@RequestBody DeliveryRequest request) {
        List<DeliveryItem> deliveryItems = new ArrayList<>();
        request.getDeliveryItemRequests().forEach(deliveryItemRequest -> {
            if (itemRepository.findById(deliveryItemRequest.getItemId()).isPresent()) {
                Item item = itemRepository.findById(deliveryItemRequest.getItemId()).get();
                DeliveryItem deliveryItem = new DeliveryItem(item, deliveryItemRequest.getQuantity());
                deliveryItemRepository.save(deliveryItem);
                deliveryItems.add(deliveryItem);
            }
        });
        Delivery delivery = new Delivery(deliveryItems, request.getScheduledFor());
        deliveryRepository.save(delivery);
        return delivery;
    }

    @GetMapping("/deliveries/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Delivery getOneDelivery(@PathVariable("id") long id) {
        checkIfDeliveryExists(id);
        return deliveryRepository.findById(id).get();
    }

    //TODO: count recommendations based on data returned by orders reports
    //@GetMapping("/deliveries/recommended-delivery")
    //public DeliveryRequest recommendDelivery() {
    //
    //}

    private void checkIfDeliveryExists(long id) {
        if (!deliveryRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such delivery doesn't exist!");
        }
    }
}

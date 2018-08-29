package com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.Item;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.ItemRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@RestController
@CrossOrigin(origins = "*")
public class DeliveryController {

    @Autowired
    private DeliveryItemRepository deliveryItemRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    @GetMapping("/deliveries/recommended-delivery")
    public List<DeliveryItemRequest> recommendDelivery() {
        List<DeliveryItemRequest> deliveryItems = new ArrayList<>();
        List<Item> items = itemRepository.findAll();
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> DAYS.between(order.getSubmissionDate(), LocalDate.now()) < 30)
                .collect(Collectors.toList());
        if(orders == null || orders.size() == 0) {
            return deliveryItems;
        }
        items.forEach(item -> {
            final int[] sum = {0};
            orders.forEach(order -> {
                order.getDeliveryItems().forEach(deliveryItem -> {
                    if(deliveryItem.getItem().getId() == item.getId()) {
                        sum[0] += deliveryItem.getQuantity();
                    }
                });
            });
            if(sum[0] > 0) {
                deliveryItems.add(new DeliveryItemRequest(item.getId(), sum[0]));
            }
        });
        return deliveryItems;
    }

    private void checkIfDeliveryExists(long id) {
        if (!deliveryRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such delivery doesn't exist!");
        }
    }
}

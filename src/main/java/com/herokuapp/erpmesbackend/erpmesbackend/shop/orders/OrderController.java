package com.herokuapp.erpmesbackend.erpmesbackend.shop.orders;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.Item;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.ItemRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryItem;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries.DeliveryItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
public class OrderController {

    private final DeliveryItemRepository deliveryItemRepository;
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    public OrderController(DeliveryItemRepository deliveryItemRepository, OrderRepository orderRepository,
                           ItemRepository itemRepository) {
        this.deliveryItemRepository = deliveryItemRepository;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
    }

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order addOneOrder(@RequestBody OrderRequest orderRequest) {
        String firstName = orderRequest.getFirstName();
        String lastName = orderRequest.getLastName();
        String email = orderRequest.getEmail();
        String phoneNumber = orderRequest.getPhoneNumber();
        String street = orderRequest.getStreet();
        String houseNumber = orderRequest.getHouseNumber();
        String city = orderRequest.getCity();
        String postalCode = orderRequest.getPostalCode();
        LocalDate scheduledFor = orderRequest.getScheduledFor();

        List<DeliveryItem> deliveryItems = new ArrayList<>();
        orderRequest.getDeliveryItemRequests().forEach(deliveryItemRequest -> {
            if (itemRepository.findById(deliveryItemRequest.getItemId()).isPresent()) {
                Item item = itemRepository.findById(deliveryItemRequest.getItemId()).get();
                DeliveryItem deliveryItem = new DeliveryItem(item, deliveryItemRequest.getQuantity());
                deliveryItemRepository.save(deliveryItem);
                deliveryItems.add(deliveryItem);
            }
        });
        Order order = new Order(firstName, lastName, email, phoneNumber, street,
                houseNumber, city, postalCode, deliveryItems, scheduledFor);
        orderRepository.save(order);
        return order;
    }

    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Order getOneOrder(@PathVariable("id") Long id) {
        checkIfOrderExists(id);
        return orderRepository.findById(id).get();
    }

    @PatchMapping("/orders/{id}")
    public HttpStatus updateStatusOrder(@PathVariable("id") Long id, @RequestBody Status status) {
        checkIfOrderExists(id);
        Order order = orderRepository.findById(id).get();

        order.setStatus(status);

        orderRepository.save(order);
        return HttpStatus.NO_CONTENT;
    }

    private void checkIfOrderExists(Long id) {
        if (!deliveryItemRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such order doesn't exist!");
        }
    }
}

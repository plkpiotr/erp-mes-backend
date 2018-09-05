package com.herokuapp.erpmesbackend.erpmesbackend.shop.orders;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ShopService shopService;

    public OrderController(OrderRepository orderRepository, ShopService shopService) {
        this.orderRepository = orderRepository;
        this.shopService = shopService;
    }

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order addOneOrder(@RequestBody ShopServiceRequest orderRequest) {
        return shopService.addNewOrder(orderRequest);
    }

    @GetMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Order getOneOrder(@PathVariable("id") Long id) {
        shopService.checkIfOrderExists(id);
        return orderRepository.findById(id).get();
    }

    @PutMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Order updateStatusOrder(@PathVariable("id") Long id, @RequestBody String status) {
        shopService.checkIfOrderExists(id);
        return shopService.updateOrderStatus(id, Status.valueOf(status));
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.shop.orders;

import com.herokuapp.erpmesbackend.erpmesbackend.emails.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ShopService shopService;
    private final EmailService emailService;

    public OrderController(OrderRepository orderRepository, ShopService shopService, EmailService emailService) {
        this.orderRepository = orderRepository;
        this.shopService = shopService;
        this.emailService = emailService;
    }

    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public Order addOneOrder(@RequestBody ShopServiceRequest orderRequest) {
        Order order = shopService.addNewOrder(orderRequest);
        emailService.sensNewOrderRegisteredMessage(order.getId());
        return order;
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
        emailService.sendOrderStatusChangeMessage(id, status);
        return shopService.updateOrderStatus(id, Status.valueOf(status));
    }
}

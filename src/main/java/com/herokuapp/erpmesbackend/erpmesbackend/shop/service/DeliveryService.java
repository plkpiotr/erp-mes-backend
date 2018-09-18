package com.herokuapp.erpmesbackend.erpmesbackend.shop.service;

import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Delivery;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.DeliveryItem;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Item;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.DeliveryItemRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.DeliveryRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.ItemRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.OrderRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.DeliveryItemRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.request.DeliveryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class DeliveryService {

    private final ItemRepository itemRepository;
    private final DeliveryItemRepository deliveryItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public DeliveryService(ItemRepository itemRepository, DeliveryItemRepository deliveryItemRepository,
                           DeliveryRepository deliveryRepository, OrderRepository orderRepository) {
        this.itemRepository = itemRepository;
        this.deliveryItemRepository = deliveryItemRepository;
        this.deliveryRepository = deliveryRepository;
        this.orderRepository = orderRepository;
    }

    public Delivery addDelivery(DeliveryRequest request) {
        List<DeliveryItem> deliveryItems = new ArrayList<>();
        request.getDeliveryItemRequests().forEach(deliveryItemRequest -> {
            addDeliveryItem(deliveryItemRequest, deliveryItems);
        });
        Delivery delivery = new Delivery(deliveryItems, request.getScheduledFor());
        deliveryRepository.save(delivery);
        return delivery;
    }

    public void addDeliveryItem(DeliveryItemRequest deliveryItemRequest, List<DeliveryItem> deliveryItems) {
        if (itemRepository.findById(deliveryItemRequest.getItemId()).isPresent()) {
            Item item = itemRepository.findById(deliveryItemRequest.getItemId()).get();
            DeliveryItem deliveryItem = new DeliveryItem(item, deliveryItemRequest.getQuantity());
            deliveryItemRepository.save(deliveryItem);
            deliveryItems.add(deliveryItem);
        }
    }


    public void checkIfDeliveryExists(long id) {
        if (!deliveryRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such delivery doesn't exist!");
        }
    }

    public List<DeliveryItemRequest> getRecommendedDelivery() {
        List<DeliveryItemRequest> deliveryItems = new ArrayList<>();
        List<Item> items = itemRepository.findAll();
        List<Order> orders = orderRepository.findAll().stream()
                .filter(order -> DAYS.between(order.getSubmissionDate(), LocalDate.now()) < 30)
                .collect(Collectors.toList());
        if (orders == null || orders.size() == 0) {
            return deliveryItems;
        }
        items.forEach(item -> {
            final int[] sum = {0};
            orders.forEach(order -> {
                order.getDeliveryItems().forEach(deliveryItem -> {
                    if (deliveryItem.getItem().getId() == item.getId()) {
                        sum[0] += deliveryItem.getQuantity();
                    }
                });
            });
            if (sum[0] > 0) {
                deliveryItems.add(new DeliveryItemRequest(item.getId(), sum[0]));
            }
        });
        return deliveryItems;
    }
}

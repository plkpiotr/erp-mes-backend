package com.herokuapp.erpmesbackend.erpmesbackend.shop.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

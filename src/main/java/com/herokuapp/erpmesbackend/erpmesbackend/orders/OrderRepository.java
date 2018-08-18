package com.herokuapp.erpmesbackend.erpmesbackend.orders;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

package com.herokuapp.erpmesbackend.erpmesbackend.shop.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}

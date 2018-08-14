package com.herokuapp.erpmesbackend.erpmesbackend.shop.deliveries;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {
}

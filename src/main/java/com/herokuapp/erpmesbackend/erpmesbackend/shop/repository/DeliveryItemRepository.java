package com.herokuapp.erpmesbackend.erpmesbackend.shop.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.DeliveryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryItemRepository extends JpaRepository<DeliveryItem, Long> {
}

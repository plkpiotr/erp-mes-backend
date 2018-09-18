package com.herokuapp.erpmesbackend.erpmesbackend.communication.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<List<Notification>> findByConsigneesId(Long id);
}

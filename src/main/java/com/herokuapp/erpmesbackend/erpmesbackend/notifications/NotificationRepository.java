package com.herokuapp.erpmesbackend.erpmesbackend.notifications;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<List<Notification>> findByConsigneesId(Long id);
}

package com.herokuapp.erpmesbackend.erpmesbackend.communication.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    long countNotificationsByTransfereeIdAndCreationTimeAfter(Long id, LocalDateTime timeRange);

    long countNotificationsByConsigneesIdAndCreationTimeAfter(Long id, LocalDateTime timeRange);

    @Query(value = "SELECT EXTRACT(EPOCH FROM (SELECT avg(n.start_time - n.creation_time) FROM notification n WHERE transferee_id = :transferee_id))",
            nativeQuery = true)
    Long countAverageDifferenceBetweenStartTimeAndCreationTime(@Param("transferee_id") Long transferee_id);

    @Query(value = "SELECT EXTRACT(EPOCH FROM (SELECT avg(n.start_time - n.creation_time) FROM notification n))",
            nativeQuery = true)
    Long countAverageDifferenceBetweenStartTimeAndCreationTime();

    @Query(value = "SELECT EXTRACT(EPOCH FROM (SELECT avg(n.end_time - n.start_time) FROM notification n WHERE transferee_id = :transferee_id))",
            nativeQuery = true)
    Long countAverageDifferenceBetweenEndTimeAndStartTime(@Param("transferee_id") Long transferee_id);

    @Query(value = "SELECT EXTRACT(EPOCH FROM (SELECT avg(n.end_time - n.start_time) FROM notification n))",
            nativeQuery = true)
    Long countAverageDifferenceBetweenEndTimeAndStartTime();

    Optional<List<Notification>> findByConsigneesId(Long id);

    Optional<List<Notification>> findByNotifierId(Long id);
}

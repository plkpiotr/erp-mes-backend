package com.herokuapp.erpmesbackend.erpmesbackend.production.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    Optional<List<Task>> findTasksByAssigneeIdAndCreationTimeAfterOrderByDeadlineAsc(Long id, LocalDateTime creationTime);
}

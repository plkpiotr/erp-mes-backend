package com.herokuapp.erpmesbackend.erpmesbackend.production.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Category;
import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    long countTasksByAssigneeIdAndCreationTimeAfter(Long id, LocalDateTime timeRange);

    long countTasksByCreationTimeAfter(LocalDateTime timeRange);

    long countTasksByAssigneeIdAndCategoryAndCreationTimeAfter(Long id, Category category, LocalDateTime timeRange);

    long countTasksByCategoryAndCreationTimeAfter(Category category, LocalDateTime timeRange);

    @Query(value = "SELECT COUNT(t.id) FROM task t WHERE t.assignee_id = :assignee_id AND t.category = 'DONE' AND" +
            " t.end_time <= t.deadline", nativeQuery = true)
    Long countDoneTasksByAssigneeIdAndEndTimeIsLessThanDeadline(@Param("assignee_id") Long assignee_id);

    @Query(value = "SELECT EXTRACT(EPOCH FROM (SELECT COUNT(t.id) FROM task t WHERE t.category = 'DONE' AND" +
            " t.end_time <= t.deadline))", nativeQuery = true)
    Long countDoneTasksByAssigneeIdAndEndTimeIsLessThanDeadline();

    @Query(value = "SELECT EXTRACT(EPOCH FROM (SELECT avg(t.start_time - t.creation_time) FROM task t WHERE" +
            " t.category = 'DOING' AND t.assignee_id = :assignee_id))", nativeQuery = true)
    Long countAverageDifferenceBetweenStartTimeAndCreationTime(@Param("assignee_id") Long assignee_id);

    @Query(value = "SELECT EXTRACT(EPOCH FROM (SELECT avg(t.start_time - t.creation_time) FROM task t WHERE" +
            " t.category = 'DOING'))", nativeQuery = true)
    Long countAverageDifferenceBetweenStartTimeAndCreationTime();

    @Query(value = "SELECT EXTRACT(EPOCH FROM (SELECT sum(t.start_time - t.creation_time) FROM task t WHERE" +
            " t.category = 'DOING' AND t.assignee_id = :assignee_id))", nativeQuery = true)
    Long countSumDifferenceBetweenStartTimeAndCreationTime(@Param("assignee_id") Long assignee_id);

    @Query(value = "SELECT EXTRACT(EPOCH FROM (SELECT sum(t.start_time - t.creation_time) FROM task t WHERE" +
            " t.category = 'DOING'))", nativeQuery = true)
    Long countSumDifferenceBetweenStartTimeAndCreationTime();

    Optional<List<Task>> findTasksByAssigneeIdAndCreationTimeAfterOrderByDeadlineAsc(Long id,LocalDateTime timeRange);

    Optional<List<Task>> findTaskByAssigneeIsNull();
}

package com.herokuapp.erpmesbackend.erpmesbackend.communication.repository;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Phase;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    long countSuggestionsByCreationTimeAfter(LocalDateTime timeRange);

    long countSuggestionsByAuthorIdAndCreationTimeAfter(Long id, LocalDateTime timeRange);

    long countSuggestionsByPhaseAndCreationTimeAfter(Phase phase, LocalDateTime timeRange);

    long countSuggestionsByAuthorIdAndPhaseAndCreationTimeAfter(Long id, Phase phase, LocalDateTime timeRange);

    Optional<List<Suggestion>> findByRecipientsId(Long id);

    Optional<List<Suggestion>> findByAuthorId(Long id);
}

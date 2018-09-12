package com.herokuapp.erpmesbackend.erpmesbackend.suggestions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {

    Optional<List<Suggestion>> findByRecipientsId(Long id);
}

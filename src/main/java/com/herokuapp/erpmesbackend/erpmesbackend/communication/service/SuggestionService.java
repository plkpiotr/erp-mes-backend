package com.herokuapp.erpmesbackend.erpmesbackend.communication.service;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.SuggestionDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Phase;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Suggestion;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.repository.SuggestionRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.SuggestionRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public SuggestionService(SuggestionRepository suggestionRepository, EmployeeRepository employeeRepository) {
        this.suggestionRepository = suggestionRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<SuggestionDTO> getAllSuggestions() {
        Employee author = employeeRepository.findByEmail(getEmailLoggedInUser()).get();
        List<Suggestion> suggestions = new ArrayList<>();
        List<Suggestion> ownSuggestions;

        if (author.isManager()) {
            suggestions = suggestionRepository.findAll();
        } else {
            if (suggestionRepository.findByRecipientsId(author.getId()).isPresent()) {
                suggestions = suggestionRepository.findByRecipientsId(author.getId()).get();
            }
            if (suggestionRepository.findByAuthorId(author.getId()).isPresent()) {
                ownSuggestions = suggestionRepository.findByAuthorId(author.getId()).get();
                suggestions.addAll(ownSuggestions);
                suggestions = suggestions.stream().distinct().collect(Collectors.toList());
            }
        }

        List<SuggestionDTO> suggestionDTOs = new ArrayList<>();
        suggestions.forEach(suggestion -> suggestionDTOs.add(new SuggestionDTO(suggestion)));
        return suggestionDTOs;
    }

    public SuggestionDTO getOneSuggestion(Long id) {
        checkIfSuggestionExists(id);
        return new SuggestionDTO(suggestionRepository.findById(id).get());
    }

    public SuggestionDTO addOneSuggestion(SuggestionRequest suggestionRequest) {
        String name = suggestionRequest.getName();
        String details = suggestionRequest.getDescription();

        Employee author = employeeRepository.findByEmail(getEmailLoggedInUser()).get();

        List<Employee> recipients = new ArrayList<>();
        suggestionRequest.getRecipientIds().forEach(this::checkIfRecipientExists);
        suggestionRequest.getRecipientIds().forEach(id -> recipients.add(employeeRepository.findById(id).get()));

        Suggestion suggestion = new Suggestion(name, details, author, recipients);
        suggestionRepository.save(suggestion);
        return new SuggestionDTO(suggestion);
    }

    public SuggestionDTO setNextPhase(Long id) {
        checkIfSuggestionExists(id);
        Suggestion suggestion = suggestionRepository.findById(id).get();

        Employee employee = employeeRepository.findByEmail(getEmailLoggedInUser()).get();

        if (suggestion.getPhase() == Phase.REPORTED) {
            suggestion.setPhase(Phase.IN_IMPLEMENTATION);
            suggestion.setStartTime(LocalDateTime.now());
            suggestion.setStartEmployee(employee);
        } else if (suggestion.getPhase() == Phase.IN_IMPLEMENTATION) {
            suggestion.setPhase(Phase.IMPLEMENTED);
            suggestion.setEndTime(LocalDateTime.now());
            suggestion.setEndEmployee(employee);
        }

        suggestionRepository.save(suggestion);
        return new SuggestionDTO(suggestion);
    }

    private String getEmailLoggedInUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails) principal).getUsername();
    }

    private void checkIfSuggestionExists(Long id) {
        if (!suggestionRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such suggestion doesn't exist!");
        }
    }

    private void checkIfRecipientExists(Long id) {
        if (!employeeRepository.findById(id).isPresent()) {
            throw new NotFoundException("At least one of the recipients doesn't exist!");
        }
    }
}

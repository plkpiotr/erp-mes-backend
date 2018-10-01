package com.herokuapp.erpmesbackend.erpmesbackend.communication.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.SuggestionDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.repository.SuggestionRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.SuggestionRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Phase;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Suggestion;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.repository.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class SuggestionController {

    private final SuggestionRepository suggestionRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public SuggestionController(SuggestionRepository suggestionRepository, EmployeeRepository employeeRepository) {
        this.suggestionRepository = suggestionRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/suggestions")
    @ResponseStatus(HttpStatus.OK)
    public List<SuggestionDTO> getAllSuggestions() {
        List<Suggestion> suggestions = suggestionRepository.findAll();
        List<SuggestionDTO> suggestionDTOs = new ArrayList<>();
        suggestions.forEach(suggestion -> suggestionDTOs.add(new SuggestionDTO(suggestion)));
        return suggestionDTOs;
    }

    @GetMapping("/suggestions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public SuggestionDTO getOneSuggestion(@PathVariable("id") Long id) {
        checkIfSuggestionExists(id);
        return new SuggestionDTO(suggestionRepository.findById(id).get());
    }

    @GetMapping("/employees/{id}/suggestions")
    @ResponseStatus(HttpStatus.OK)
    public List<SuggestionDTO> getSuggestionsByRecipient(@PathVariable("id") Long id) {
        if (!suggestionRepository.findByRecipientsId(id).isPresent())
            return new ArrayList<>();
        List<Suggestion> suggestions = suggestionRepository.findByRecipientsId(id).get();
        List<SuggestionDTO> suggestionDTOs = new ArrayList<>();
        suggestions.forEach(suggestion -> suggestionDTOs.add(new SuggestionDTO(suggestion)));
        return suggestionDTOs;
    }

    @PostMapping("/suggestions")
    @ResponseStatus(HttpStatus.CREATED)
    public SuggestionDTO addOneSuggestion(@RequestBody SuggestionRequest suggestionRequest) {
        String name = suggestionRequest.getName();
        String details = suggestionRequest.getDescription();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Employee author = employeeRepository.findByEmail(username).get();

        List<Employee> recipients = new ArrayList<>();
        suggestionRequest.getRecipientIds().forEach(this::checkIfRecipientExists);
        suggestionRequest.getRecipientIds().forEach(id -> recipients.add(employeeRepository.findById(id).get()));

        Suggestion suggestion = new Suggestion(name, details, author, recipients);
        suggestionRepository.save(suggestion);
        return new SuggestionDTO(suggestion);
    }

    @PatchMapping("suggestions/{id}")
    public HttpStatus updateStateNotification(@PathVariable("id") Long id, @RequestBody Phase phase) {
        checkIfSuggestionExists(id);
        Suggestion suggestion = suggestionRepository.findById(id).get();

        suggestion.setPhase(phase);

        suggestionRepository.save(suggestion);
        return HttpStatus.NO_CONTENT;
    }

    private void checkIfSuggestionExists(Long id) {
        if (!suggestionRepository.findById(id).isPresent())
            throw new NotFoundException("Such suggestion doesn't exist!");
    }

    private void checkIfAuthorExists(Long id) {
        if (!employeeRepository.findById(id).isPresent())
            throw new NotFoundException("Chosen author doesn't exist!");
    }

    private void checkIfRecipientExists(Long id) {
        if (!employeeRepository.findById(id).isPresent())
            throw new NotFoundException("At least one of the recipients doesn't exist!");
    }
}

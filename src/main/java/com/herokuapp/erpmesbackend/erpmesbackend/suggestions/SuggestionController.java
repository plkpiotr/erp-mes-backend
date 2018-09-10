package com.herokuapp.erpmesbackend.erpmesbackend.suggestions;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("http://localhost:4200")
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
    public List<Suggestion> getAllSuggestions() {
        return new ArrayList<>(suggestionRepository.findAll());
    }

    @GetMapping("/suggestions/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Suggestion getOneSuggestion(@PathVariable("id") Long id) {
        checkIfSuggestionExists(id);
        return suggestionRepository.findById(id).get();
    }

    @GetMapping("/employees/{id}/suggestions")
    @ResponseStatus(HttpStatus.OK)
    public List<Suggestion> getSuggestionsByRecipient(@PathVariable("id") Long id) {
        return suggestionRepository.findByRecipientsContaining(id).isPresent() ?
                suggestionRepository.findByRecipientsContaining(id).get() : new ArrayList<>();
    }

    @PostMapping("/suggestions")
    @ResponseStatus(HttpStatus.CREATED)
    public Suggestion addOneSuggestion(@RequestBody SuggestionRequest suggestionRequest) {
        String name = suggestionRequest.getName();
        String details = suggestionRequest.getDescription();

        Employee author = null;
        if (suggestionRequest.getAuthorId() != null) {
            checkIfAuthorExists(suggestionRequest.getAuthorId());
            author = employeeRepository.findById(suggestionRequest.getAuthorId()).get();
        }

        List<Employee> recipients = new ArrayList<>();
        suggestionRequest.getRecipientIds().forEach(this::checkIfRecipientExists);
        suggestionRequest.getRecipientIds().forEach(id -> recipients.add(employeeRepository.findById(id).get()));

        Suggestion suggestion = new Suggestion(name, details, author, recipients);
        suggestionRepository.save(suggestion);
        return suggestion;
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

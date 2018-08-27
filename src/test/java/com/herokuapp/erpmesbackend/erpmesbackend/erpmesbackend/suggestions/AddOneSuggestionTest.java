package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.suggestions;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.Suggestion;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.SuggestionRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddOneSuggestionTest extends FillBaseTemplate {

    private Suggestion suggestion;

    @Before
    public void init() {
        addEmployeeRequests(true);

        String name = suggestionFactory.generateName();
        String description = suggestionFactory.generateDescription();

        Employee author = restTemplate.getForEntity("/employees/{id}", Employee.class, 1).getBody();
        Long authorId = author.getId();

        List<Employee> recipients = new ArrayList<>();
        List<Long> recipientIds = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            Employee consignee = restTemplate.getForEntity("/employees/{id}", Employee.class, i).getBody();
            recipientIds.add(consignee.getId());
            recipients.add(consignee);
        }

        suggestionRequest = new SuggestionRequest(name, description, authorId, recipientIds);
        suggestion = new Suggestion(name, description, author, recipients);
    }

    @Test
    public void checkIfResponseContainsAddedSuggestion() {
        ResponseEntity<Suggestion> suggestionResponseEntity = restTemplate.postForEntity("/suggestions",
                suggestionRequest, Suggestion.class);
        assertThat(suggestionResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Suggestion body = suggestionResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(suggestion));
    }
}

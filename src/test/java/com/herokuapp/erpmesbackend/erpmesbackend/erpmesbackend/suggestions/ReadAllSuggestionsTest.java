package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.suggestions;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.Suggestion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadAllSuggestionsTest extends FillBaseTemplate {

    private List<Suggestion> suggestions;

    @Before
    public void init() {
        addEmployeeRequests(true);
        suggestions = addSuggestionRequests(true);
    }

    @Test
    public void checkIfResponseContainsAllSuggestions() {
        ResponseEntity<Suggestion[]> forEntity = restTemplate.getForEntity("/suggestions", Suggestion[].class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<Suggestion> fetchedSuggetstions = Arrays.asList(forEntity.getBody());
        for (Suggestion sug : fetchedSuggetstions) {
            assertTrue(suggestions.stream().anyMatch(s -> s.checkIfDataEquals(sug)));
        }
    }
}

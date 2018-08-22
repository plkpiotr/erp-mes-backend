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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadOneSuggestionTest extends FillBaseTemplate {

    private List<Suggestion> suggestions;

    @Before
    public void init() {
        addOneAdminRequest(true);
        addOneEmployeeRequest(true);
        addOneOrderRequest(true);
        suggestions = addSuggestionRequests(true);
    }

    @Test
    public void checkIfResponseContainsSuggestionWithGivenId() {
        for (int i = 0; i < 3; i++) {
            ResponseEntity<Suggestion> forEntity = restTemplate.getForEntity("/suggestions/{id}",
                    Suggestion.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

            Suggestion suggestion = forEntity.getBody();
            assertTrue(suggestions.stream().anyMatch(s -> s.checkIfDataEquals(suggestion)));
        }
    }
}

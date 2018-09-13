package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.suggestions;

import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.SuggestionDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadOneSuggestionTest extends FillBaseTemplate {

    private List<SuggestionDTO> suggestionDTOs;

    @Before
    public void init() {
        setupToken();
        addEmployeeRequests(true);
        suggestionDTOs = addSuggestionRequests(true);
    }

    @Test
    public void checkIfResponseContainsSuggestionWithGivenId() {
        for (int i = 0; i < 3; i++) {
            ResponseEntity<SuggestionDTO> forEntity = restTemplate.exchange("/suggestions/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), SuggestionDTO.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

            SuggestionDTO suggestionDTO = forEntity.getBody();
            assertTrue(suggestionDTOs.stream().anyMatch(s -> s.checkIfDataEquals(suggestionDTO)));
        }
    }
}

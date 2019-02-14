package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.SuggestionDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Phase;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.SuggestionRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SuggestionControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupSuggestions();
    }

    @Test
    public void readAllSuggestionsTest() {
        ResponseEntity<SuggestionDTO[]> forEntity = restTemplate.exchange("/suggestions", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), SuggestionDTO[].class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<SuggestionDTO> suggestionDTOS = Arrays.asList(forEntity.getBody());
        suggestionRequests.forEach(request -> suggestionDTOS.stream()
                .anyMatch(suggestionDTO -> checkIfSuggestionDtoAndRequestMatch(suggestionDTO, request))
        );
    }

    @Test
    public void readOneSuggestionTest() {
        for (int i = 0; i < suggestionRequests.size(); i++) {
            ResponseEntity<SuggestionDTO> forEntity = restTemplate.exchange("/suggestions/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), SuggestionDTO.class, i + 1);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(forEntity.getBody()).isNotNull();
        }
    }

    @Test
    public void addOneSuggestionTest() {
        Long[] ids = {2L, 3L};
        SuggestionRequest oneSuggestionRequestWithIds = getOneSuggestionRequestWithIds(ids);
        ResponseEntity<SuggestionDTO> suggestionDTOResponseEntity = restTemplate.postForEntity("/suggestions",
                new HttpEntity<>(oneSuggestionRequestWithIds, requestHeaders), SuggestionDTO.class);
        assertThat(suggestionDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertTrue(checkIfSuggestionDtoAndRequestMatch(suggestionDTOResponseEntity.getBody(),
                oneSuggestionRequestWithIds));
    }

    @Test
    public void updateSuggestionPhaseTest() {
        ResponseEntity<SuggestionDTO> exchange = restTemplate.exchange("/suggestions/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), SuggestionDTO.class, 1);

        assertThat(exchange.getBody().getPhase()).isEqualTo(Phase.REPORTED);

        ResponseEntity<SuggestionDTO> suggestionResponseEntity = restTemplate.exchange("/suggestions/{id}",
                HttpMethod.PUT, new HttpEntity<>(Phase.IN_IMPLEMENTATION.name(), requestHeaders),
                SuggestionDTO.class, 1);

        assertThat(suggestionResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(suggestionResponseEntity.getBody().getPhase()).isEqualTo(Phase.IN_IMPLEMENTATION);
    }

    @Test
    public void shouldReturn404NotFound() {
        ResponseEntity<String> forEntity = restTemplate.exchange("/suggestions/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private boolean checkIfSuggestionDtoAndRequestMatch(SuggestionDTO suggestionDTO, SuggestionRequest request) {
        return suggestionDTO.getDescription().equals(request.getDescription()) &&
                suggestionDTO.getName().equals(request.getName()) &&
                suggestionDTO.getRecipients().size() == request.getRecipientIds().size();
    }
}

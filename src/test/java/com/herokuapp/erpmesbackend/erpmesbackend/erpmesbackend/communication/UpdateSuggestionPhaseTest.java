package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.communication;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Phase;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.Suggestion;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UpdateSuggestionPhaseTest extends FillBaseTemplate {

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void checkIfSuggestionPhaseHasChanged() {
        ResponseEntity<Suggestion> exchange = restTemplate.exchange("/suggestions/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), Suggestion.class, 1);

        assertThat(exchange.getBody().getPhase()).isEqualTo(Phase.REPORTED);

        ResponseEntity<Suggestion> suggestionResponseEntity = restTemplate.exchange("/suggestions/{id}",
                HttpMethod.PUT, new HttpEntity<>(Phase.IN_IMPLEMENTATION.name(), requestHeaders),
                Suggestion.class, 1);

        assertThat(suggestionResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(suggestionResponseEntity.getBody().getPhase()).isEqualTo(Phase.IN_IMPLEMENTATION);
    }
}

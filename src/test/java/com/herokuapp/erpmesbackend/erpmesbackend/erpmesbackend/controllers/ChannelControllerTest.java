package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.controllers;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.ChannelDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.ChannelRequest;
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
public class ChannelControllerTest extends TestConfig {

    @Before
    public void init() {
        setup();
        setupToken();
        setupChannels();
    }

    @Test
    public void readAllChannelsTest() {
        ResponseEntity<ChannelDTO[]> forEntity = restTemplate.exchange("/channels", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), ChannelDTO[].class);
        assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<ChannelDTO> fetchedSuggestions = Arrays.asList(forEntity.getBody());
        channelRequests.forEach(request -> fetchedSuggestions.stream()
                .anyMatch(channelDTO -> checkIfChannelDtoAndRequestMatch(channelDTO, request))
        );
    }

    @Test
    public void readOneChannelTest() {
        for (int i = 0; i < channelRequests.size(); i++) {
            ResponseEntity<ChannelDTO> exchange = restTemplate.exchange("/channels/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), ChannelDTO.class, i+1);
            assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(exchange.getBody()).isNotNull();
        }
    }

    @Test
    public void addOneChannelTest() {
        Long[] ids = {2L, 3L};
        ChannelRequest oneChannelRequestWithIds = getOneChannelRequestWithIds(ids);
        ResponseEntity<ChannelDTO> channelDTOResponseEntity = restTemplate.postForEntity("/channels",
                new HttpEntity<>(oneChannelRequestWithIds, requestHeaders), ChannelDTO.class);
        assertThat(channelDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertTrue(checkIfChannelDtoAndRequestMatch(channelDTOResponseEntity.getBody(), oneChannelRequestWithIds));
    }

    @Test
    public void shuldReturn404NotFound() {
        ResponseEntity<String> exchange = restTemplate.exchange("/channels/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), String.class, 1234);
        assertThat(exchange.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private boolean checkIfChannelDtoAndRequestMatch(ChannelDTO channelDTO, ChannelRequest request) {
        return channelDTO.getName().equals(request.getName()) &&
                channelDTO.getParticipantDTOs().size() == request.getParticipantIds().size();
    }
}

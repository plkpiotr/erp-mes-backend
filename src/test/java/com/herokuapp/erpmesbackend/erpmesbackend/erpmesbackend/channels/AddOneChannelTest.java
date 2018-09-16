package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.channels;

import com.herokuapp.erpmesbackend.erpmesbackend.chat.ChannelDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.chat.ChannelRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.FillBaseTemplate;
import com.herokuapp.erpmesbackend.erpmesbackend.suggestions.Suggestion;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddOneChannelTest extends FillBaseTemplate {

    private ChannelDTO channelDTO;

    @Before
    public void init() {
        setupToken();
        addNonAdminRequests(true);

        String name = channelFactory.generateName();

        List<EmployeeDTO> participantDTOs = new ArrayList<>();
        List<Long> participantIds = new ArrayList<>();

        for (int i = 1; i < 5; i++) {
            EmployeeDTO participantDTO = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, i).getBody();
            participantIds.add(participantDTO.getId());
            participantDTOs.add(participantDTO);
        }

        channelRequest = new ChannelRequest(name, participantIds);
        channelDTO = new ChannelDTO(name, participantDTOs);
    }

    @Test
    public void checkIfResponseContainsAddedChannel() {
        ResponseEntity<ChannelDTO> channelDTOResponseEntity = restTemplate.postForEntity("/channels",
                new HttpEntity<>(channelRequest, requestHeaders), ChannelDTO.class);
        assertThat(channelDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ChannelDTO body = channelDTOResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(channelDTO));
    }
}

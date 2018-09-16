package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.channels;

import com.herokuapp.erpmesbackend.erpmesbackend.chat.ChannelDTO;
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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReadAllChannelsTest extends FillBaseTemplate {

    private List<ChannelDTO> channelDTOs;

    @Before
    public void init() {
        setupToken();
        addNonAdminRequests(true);
        channelDTOs = addChannelRequests(true);
    }

    @Test
    public void checkIfResponseContainsAllChannels() {
        for (int i = 1; i < 4; i++) {
            ResponseEntity<ChannelDTO[]> forEntity = restTemplate.exchange("/channels", HttpMethod.GET,
                    new HttpEntity<>(null, requestHeaders), ChannelDTO[].class);
            assertThat(forEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

            List<ChannelDTO> fetchedSuggetstions = Arrays.asList(forEntity.getBody());
            for (ChannelDTO channelDTO : fetchedSuggetstions) {
                assertTrue(channelDTOs.stream().anyMatch(s -> s.checkIfDataEquals(channelDTO)));
            }
        }
    }
}
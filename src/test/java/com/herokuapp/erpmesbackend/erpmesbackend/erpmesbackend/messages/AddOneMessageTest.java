package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.messages;

import com.herokuapp.erpmesbackend.erpmesbackend.chat.ChannelDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.chat.MessageDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.chat.MessageRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeDTO;
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
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddOneMessageTest extends FillBaseTemplate {

    private MessageDTO messageDTO;

    @Before
    public void init() {
        setupToken();
        addNonAdminRequests(true);
        addOneChannelRequest(true);

        String content = messageFactory.generateContent();

        EmployeeDTO authorDTO = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody();

        ChannelDTO channelDTO = restTemplate.exchange("/channels/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), ChannelDTO.class, 1).getBody();

        messageRequest = new MessageRequest(content);
        messageDTO = new MessageDTO(content, authorDTO, channelDTO);
    }

    @Test
    public void checkIfResponseContainsAddedMessage() {
        ResponseEntity<MessageDTO> messageDTOResponseEntity = restTemplate.postForEntity("/messages",
                new HttpEntity<>(messageRequest, requestHeaders), MessageDTO.class);
        assertThat(messageDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        MessageDTO body = messageDTOResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(messageDTO));
    }
}

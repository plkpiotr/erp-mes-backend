package com.herokuapp.erpmesbackend.erpmesbackend.erpmesbackend.communication;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.dto.MessageDTO;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.MessageRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.dto.EmployeeDTO;
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
        super.init();
        String content = messageFactory.generateContent();

        EmployeeDTO authorDTO = restTemplate.exchange("/employees/{id}", HttpMethod.GET,
                new HttpEntity<>(null, requestHeaders), EmployeeDTO.class, 1).getBody();

        Long channelId = 1L;

        messageRequest = new MessageRequest(content);
        messageDTO = new MessageDTO(content, authorDTO, channelId);
    }

    @Test
    public void checkIfResponseContainsAddedMessage() {
        ResponseEntity<MessageDTO> messageDTOResponseEntity = restTemplate.postForEntity("/messages/1",
                new HttpEntity<>(messageRequest, requestHeaders), MessageDTO.class);
        assertThat(messageDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        MessageDTO body = messageDTOResponseEntity.getBody();
        assertTrue(body.checkIfDataEquals(messageDTO));
    }
}

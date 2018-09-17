package com.herokuapp.erpmesbackend.erpmesbackend.chat;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public MessageController(MessageRepository messageRepository, ChannelRepository channelRepository,
                             EmployeeRepository employeeRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/messages/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<MessageDTO> getMessagesByChannelId(@PathVariable ("id") Long id) {
        checkIfChannelExists(id);
        if (!messageRepository.findMessageByChannelIdOrderByCreationTimeDesc(id).isPresent())
            return new ArrayList<>();
        List<Message> messages = messageRepository.findMessageByChannelIdOrderByCreationTimeDesc(id).get();
        List<MessageDTO> messageDTOs = new ArrayList<>();
        messages.forEach(message -> messageDTOs.add(new MessageDTO(message)));
        return messageDTOs;
    }

    @PostMapping("/messages/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDTO addOneMessage(@RequestBody MessageRequest messageRequest, @PathVariable ("id") Long id) {
        checkIfChannelExists(id);

        String content = messageRequest.getContent();

        // Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // String username = ((UserDetails) principal).getUsername();
        // TODO: http://bit.ly/2MDblSR
        Employee author = employeeRepository.findById(1L).get();

        Message message = new Message(content, author, id);
        messageRepository.save(message);
        return new MessageDTO(message);
    }

    private void checkIfChannelExists(Long id) {
        if (!channelRepository.findById(id).isPresent())
            throw new NotFoundException("Such channel doesn't exist!");
    }
}

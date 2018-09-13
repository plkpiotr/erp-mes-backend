package com.herokuapp.erpmesbackend.erpmesbackend.chat;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:4200")
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

    @PostMapping("/channels/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public Message addOneMessage(@PathVariable("id") Long id, @RequestBody MessageRequest messageRequest) {
        checkIfChannelExists(id);

        String content = messageRequest.getContent();

        checkIfChannelExists(id);
        Channel channel = channelRepository.findById(id).get();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Employee author = employeeRepository.findByEmail(username).get();

        Message message = new Message(content, author, channel);
        messageRepository.save(message);
        return message;
    }

    private void checkIfChannelExists(Long id) {
        if (!channelRepository.findById(id).isPresent())
            throw new NotFoundException("Such channel doesn't exist!");
    }
}

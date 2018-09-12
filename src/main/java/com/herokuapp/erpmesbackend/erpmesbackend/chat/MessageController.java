package com.herokuapp.erpmesbackend.erpmesbackend.chat;

import com.herokuapp.erpmesbackend.erpmesbackend.employees.Employee;
import com.herokuapp.erpmesbackend.erpmesbackend.employees.EmployeeRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.ForbiddenException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:4200")
public class MessageController {

    private final ChannelRepository channelRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public MessageController(ChannelRepository channelRepository, MessageRepository messageRepository, EmployeeRepository employeeRepository) {
        this.channelRepository = channelRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/channels/{id}/messages")
    @ResponseStatus(HttpStatus.CREATED)
    public Message addOneMessage(@PathVariable("id") Long id, @RequestBody MessageRequest messageRequest) {
        checkIfChannelExists(id);

        String content = messageRequest.getContent();

        checkIfEmployeeIsLoggedIn();
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        Employee author = employeeRepository.findByEmail(messageRequest.getAuthorUsername()).get();

        // TODO: Change on the basis of dependency with channel/id
        return new Message();
    }

    private void checkIfChannelExists(Long id) {
        if (!channelRepository.findById(id).isPresent())
            throw new NotFoundException("Such channel doesn't exist!");
    }

    // TODO: Check in project at work
    private void checkIfEmployeeIsLoggedIn() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        if (!employeeRepository.findByEmail(username).isPresent())
            throw new ForbiddenException("You are logged out!");
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.communication.controller;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.repository.EmailEntityRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.EmailEntityRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.service.EmailService;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailEntity;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
public class EmailController {

    private final EmailEntityRepository emailEntityRepository;
    private final EmailService emailService;

    @Autowired
    public EmailController(EmailEntityRepository emailEntityRepository, EmailService emailService) {
        this.emailEntityRepository = emailEntityRepository;
        this.emailService = emailService;
    }

    @GetMapping("/emails/inbox")
    @ResponseStatus(HttpStatus.OK)
    public List<EmailEntity> readMail() {
        return emailService.readMail();
    }

    @GetMapping("/emails/outbox")
    @ResponseStatus(HttpStatus.OK)
    public List<EmailEntity> readSentMail() {
        return emailEntityRepository.findAll(new Sort(Sort.Direction.DESC, "timestamp"))
                .stream()
                .filter(emailEntity -> emailEntity.getEmailType().equals(EmailType.SENT))
                .collect(Collectors.toList());
    }

    @PostMapping("/emails")
    @ResponseStatus(HttpStatus.CREATED)
    public EmailEntity sendMailWithAddress(@RequestBody EmailEntityRequest request) {
        return emailService.sendMessage(request.getEmail(), request.getSubject(), request.getContent());
    }

    @GetMapping("/emails/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<EmailEntity> readConversation(@PathVariable("id") long id) {
        emailService.checkIfEmailExists(id);
        EmailEntity emailEntity = emailEntityRepository.findById(id).get();
        return emailEntityRepository.findByEmailOrderByTimestampDesc(emailEntity.getEmail()).get();
    }

    @PostMapping("/emails/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public EmailEntity sendMailWithAddrss(@RequestBody EmailEntityRequest request,
                                          @PathVariable("id") long id) {
        emailService.checkIfEmailExists(id);
        emailService.checkIfEmailReceived(id);
        EmailEntity emailEntity = emailEntityRepository.findById(id).get();
        return emailService.sendMessage(emailEntity.getEmail(), request.getSubject(),
                request.getContent());
    }

    @DeleteMapping("/emails/{id}")
    public HttpStatus removeEmail(@PathVariable("id") long id) {
        emailService.checkIfEmailExists(id);
        emailEntityRepository.delete(emailEntityRepository.findById(id).get());
        return HttpStatus.NO_CONTENT;
    }
}

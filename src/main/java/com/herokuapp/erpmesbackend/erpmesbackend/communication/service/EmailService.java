package com.herokuapp.erpmesbackend.erpmesbackend.communication.service;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailEntity;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailType;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.repository.EmailEntityRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.InvalidRequestException;
import com.herokuapp.erpmesbackend.erpmesbackend.exceptions.NotFoundException;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Complaint;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.ComplaintRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.OrderRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Return;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.repository.ReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private final String FOOTER = "\n\n This is an automatically generated message. Please do not respond." +
            "\n\n Sincerely, ERP-MES Inc.";

    @Autowired
    private JavaMailSender javaMailSender;

    private final EmailEntityRepository emailEntityRepository;
    private final OrderRepository orderRepository;
    private final ReturnRepository returnRepository;
    private final ComplaintRepository complaintRepository;

    @Autowired
    public EmailService(EmailEntityRepository emailEntityRepository,
                        OrderRepository orderRepository, ReturnRepository returnRepository,
                        ComplaintRepository complaintRepository) {
        this.emailEntityRepository = emailEntityRepository;
        this.orderRepository = orderRepository;
        this.returnRepository = returnRepository;
        this.complaintRepository = complaintRepository;
    }

    public void sensNewOrderRegisteredMessage(long id) {
        Order order = orderRepository.findById(id).get();
        String subject = "New order registered.";
        String details = "Hello, " + order.getFirstName() + "! \n\n We have successfully registered " +
                "your new order (id: " + order.getId() + "). Its current status is: " + order.getStatus() +
                "." + FOOTER;
        sendMessage(order.getEmail(), subject, Arrays.asList(details));
    }

    public void sendOrderStatusChangeMessage(long id, String status) {
        Order order = orderRepository.findById(id).get();
        String subject = "Status change for order " + order.getId();
        String details = "Hello, " + order.getFirstName() + "! \n\n The state of your order (id: " +
                order.getId() + ") has been changed to " + status + "." + FOOTER;
        sendMessage(order.getEmail(), subject, Arrays.asList(details));
    }

    public void sendNewReturnRegisteredMessage(long id) {
        Return r = returnRepository.findById(id).get();
        String subject = "New return registered.";
        String details = "Hello, " + r.getFirstName() + "! \n\n We have successfully registered " +
                "your new return (id: " + r.getId() + "). Its current status is: " + r.getStatus() +
                "." + FOOTER;
        sendMessage(r.getEmail(), subject, Arrays.asList(details));
    }

    public void sendReturnStatusChangeMessage(long id, String status) {
        Return r = returnRepository.findById(id).get();
        String subject = "Status change for return " + r.getId();
        String details = "Hello, " + r.getFirstName() + "! \n\n The state of your return (id: " +
                r.getId() + ") has been changed to " + status + "." + FOOTER;
        sendMessage(r.getEmail(), subject, Arrays.asList(details));
    }

    public void sensNewComplaintRegisteredMessage(long id) {
        Complaint complaint = complaintRepository.findById(id).get();
        String subject = "New complaint registered.";
        String details = "Hello, " + complaint.getFirstName() + "! \n\n We have successfully registered " +
                "your new complaint (id: " + complaint.getId() + "). Its current status is: " +
                complaint.getStatus() + "." + FOOTER;
        sendMessage(complaint.getEmail(), subject, Arrays.asList(details));
    }

    public void sendComplaintStatusChangeMessage(long id, String status) {
        Complaint complaint = complaintRepository.findById(id).get();
        String subject = "Status change for complaint " + complaint.getId();
        String details = "Hello, " + complaint.getFirstName() + "! \n\n The state of your complaint (id: " +
                complaint.getId() + ") has been changed to " + status + ". ";
        if (status.equals("ACCEPTED")) {
            details += "You will get your resolution details in another message";
        }
        details += FOOTER;
        sendMessage(complaint.getEmail(), subject, Arrays.asList(details));
    }

    public void sendComplaintResolutionChangeMessage(long id, String resolution) {
        Complaint complaint = complaintRepository.findById(id).get();
        String subject = "Resolution change for complaint " + complaint.getId();
        String details = "Hello, " + complaint.getFirstName() + "! \n\n The resolution of your complaint (id: " +
                complaint.getId() + ") will be: " + resolution + "." + FOOTER;
        sendMessage(complaint.getEmail(), subject, Arrays.asList(details));
    }

    public EmailEntity sendMessage(String to, String subject, List<String> content) {
        SimpleMailMessage message = new SimpleMailMessage();

        String text = "";
        for (String part: content) {
            text += part;
        }
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        EmailEntity emailEntity = new EmailEntity(to, subject, content, EmailType.SENT,
                LocalDateTime.now());
        emailEntityRepository.save(emailEntity);

        try {
            javaMailSender.send(message);
        } catch (MailException me) {
            me.printStackTrace();
        }
        return emailEntity;
    }

    public List<EmailEntity> readMail() {
        emailEntityRepository.findAll().forEach(emailEntity -> {
            if (emailEntity.getEmailType().equals(EmailType.RECEIVED)) {
                emailEntityRepository.delete(emailEntity);
            }
        });
        InboxService inboxService = new InboxService();
        List<EmailEntity> emailEntities = inboxService.readMail();
        emailEntities.forEach(emailEntity -> emailEntityRepository.save(emailEntity));
        return emailEntityRepository.findAll(new Sort(Sort.Direction.DESC, "timestamp"))
                .stream()
                .filter(emailEntity -> emailEntity.getEmailType().equals(EmailType.RECEIVED))
                .collect(Collectors.toList());
    }

    public void checkIfEmailExists(long id) {
        if (!emailEntityRepository.findById(id).isPresent()) {
            throw new NotFoundException("Such emails doesn't exist!");
        }
    }

    public void checkIfEmailReceived(long id) {
        if (emailEntityRepository.findById(id).get().getEmailType().equals(EmailType.SENT)) {
            throw new InvalidRequestException("Don't attempt to reply to yourself!");
        }
    }
}

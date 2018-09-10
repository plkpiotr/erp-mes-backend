package com.herokuapp.erpmesbackend.erpmesbackend.email;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints.Complaint;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.complaints.ComplaintRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.Order;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.orders.OrderRepository;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.returns.Return;
import com.herokuapp.erpmesbackend.erpmesbackend.shop.returns.ReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final String FOOTER = "\n\n This is an automatically generated message. Please do not respond." +
            "\n\n Sincerely, ERP-MES Inc.";

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailEntityRepository emailEntityRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ReturnRepository returnRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    public void sensNewOrderRegisteredMessage(long id) {
        Order order = orderRepository.findById(id).get();
        String subject = "New order registered.";
        String details = "Hello, " + order.getFirstName() + "! \n\n We have successfully registered " +
                "your new order (id: " + order.getId() + "). Its current status is: " + order.getStatus() +
                "." + FOOTER;
        sendMessage(order.getEmail(), subject, details);
    }

    public void sendOrderStatusChangeMessage(long id, String status) {
        Order order = orderRepository.findById(id).get();
        String subject = "Status change for order " + order.getId();
        String details = "Hello, " + order.getFirstName() + "! \n\n The state of your order (id: " +
                order.getId() + ") has been changed to " + status + "." + FOOTER;
        sendMessage(order.getEmail(), subject, details);
    }

    public void sensNewReturnRegisteredMessage(long id) {
        Return r = returnRepository.findById(id).get();
        String subject = "New return registered.";
        String details = "Hello, " + r.getFirstName() + "! \n\n We have successfully registered " +
                "your new return (id: " + r.getId() + "). Its current status is: " + r.getStatus() +
                "." + FOOTER;
        sendMessage(r.getEmail(), subject, details);
    }

    public void sendReturnStatusChangeMessage(long id, String status) {
        Return r = returnRepository.findById(id).get();
        String subject = "Status change for return " + r.getId();
        String details = "Hello, " + r.getFirstName() + "! \n\n The state of your return (id: " +
                r.getId() + ") has been changed to " + status + "." + FOOTER;
        sendMessage(r.getEmail(), subject, details);
    }

    public void sensNewComplaintRegisteredMessage(long id) {
        Complaint complaint = complaintRepository.findById(id).get();
        String subject = "New complaint registered.";
        String details = "Hello, " + complaint.getFirstName() + "! \n\n We have successfully registered " +
                "your new complaint (id: " + complaint.getId() + "). Its current status is: " +
                complaint.getStatus() + "." + FOOTER;
        sendMessage(complaint.getEmail(), subject, details);
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
        sendMessage(complaint.getEmail(), subject, details);
    }

    public void sendComplaintResolutionChangeMessage(long id, String resolution) {
        Complaint complaint = complaintRepository.findById(id).get();
        String subject = "Resolution change for complaint " + complaint.getId();
        String details = "Hello, " + complaint.getFirstName() + "! \n\n The resolution of your complaint (id: " +
                complaint.getId() + ") will be: " + resolution + "." + FOOTER;
        sendMessage(complaint.getEmail(), subject, details);
    }

    public void sendMessage(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);

            EmailEntity emailEntity = new EmailEntity(to, subject, content);
            emailEntityRepository.save(emailEntity);

            javaMailSender.send(message);
        } catch (MailException me) {
            me.printStackTrace();
        }
    }
}

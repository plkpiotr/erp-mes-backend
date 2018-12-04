package com.herokuapp.erpmesbackend.erpmesbackend.communication.service;


import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailEntity;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailType;

import javax.mail.*;
import javax.mail.Message;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class InboxService {

    public List<EmailEntity> readMail() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "gmail");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.ssl.trust", "*");

        Session session = Session.getDefaultInstance(props, null);
        List<EmailEntity> emailEntityList = new ArrayList<>();

        try {
            Store store = session.getStore("imaps");
            store.connect("smtp.gmail.com", "erp.mes123@gmail.com", "erpmeserpmes");

            Folder folder = store.getFolder("inbox");
            folder.open(Folder.READ_ONLY);
            javax.mail.Message[] messages = folder.getMessages();

            for (Message message : messages) {
                String address = message.getFrom()[0].toString();
                String emailAddress = address.substring(address.indexOf("<") + 1,
                        address.lastIndexOf(">"));
                Date receivedDate = message.getReceivedDate();
                Instant instant = receivedDate.toInstant();
                ZoneId zoneId = ZoneId.systemDefault();
                LocalDateTime receivedDateTime = instant.atZone(zoneId).toLocalDateTime();

                List<String> content = new ArrayList<>();
                Multipart multipart = (Multipart) message.getContent();
                int j = 0;
                boolean reachedHtml = false;
                while (j < multipart.getCount() && !reachedHtml) {
                    BodyPart bodyPart = multipart.getBodyPart(j);
                    String contentPart = (String) bodyPart.getContent();
                    if (contentPart.contains("html")) {
                        reachedHtml = true;
                    } else {
                        if (contentPart.length() >= 255) {
                            for (int i = 0; i < contentPart.length()/255; i++) {
                                content.add(contentPart.substring(i*255, (i+1)*255));
                            }
                        } else {
                            content.add(contentPart);
                        }
                    }
                    j++;
                }
                emailEntityList.add(new EmailEntity(emailAddress, message.getSubject(), content,
                        EmailType.RECEIVED, receivedDateTime));
            }
            folder.close(true);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emailEntityList;
    }
}

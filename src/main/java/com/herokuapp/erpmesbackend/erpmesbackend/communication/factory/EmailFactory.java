package com.herokuapp.erpmesbackend.erpmesbackend.communication.factory;

import com.herokuapp.erpmesbackend.erpmesbackend.communication.request.EmailEntityRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailEntity;
import com.herokuapp.erpmesbackend.erpmesbackend.communication.model.EmailType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

public class EmailFactory {

    private final String CONTENT = "Random content";
    private final String SUBJECT = "Random subject";
    private final Random r;

    public EmailFactory() {
        r = new Random();
    }

    public EmailEntityRequest generateRequest() {
        String randomNumberSuffix = String.valueOf(r.nextInt(50));
        return new EmailEntityRequest(SUBJECT + randomNumberSuffix,
                Arrays.asList(CONTENT + randomNumberSuffix));
    }

    public EmailEntityRequest generateRequestWithAddress() {
        String randomNumberSuffix = String.valueOf(r.nextInt(50));
        return new EmailEntityRequest("testmail.erp.mes123@gmail.com",
                SUBJECT + randomNumberSuffix,
                Arrays.asList(CONTENT + randomNumberSuffix));
    }

    public EmailEntity generateSentEmail() {
        EmailEntityRequest request = generateRequestWithAddress();
        return new EmailEntity(request.getEmail(), request.getSubject(), request.getContent(),
                EmailType.SENT, LocalDateTime.now());
    }

    public EmailEntity generateReceivedEmail() {
        EmailEntityRequest request = generateRequestWithAddress();
        return new EmailEntity(request.getEmail(), request.getSubject(), request.getContent(),
                EmailType.RECEIVED, LocalDateTime.now());
    }
}

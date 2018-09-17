package com.herokuapp.erpmesbackend.erpmesbackend.chat;

import java.util.Random;

public class MessageFactory {

    private final String[] CONTENTS = {"Cześć", "Dzień dobry", "Pozdrawiam", "Dziękuję", "Przepraszam"};

    private final Random random;

    public MessageFactory() {
        random = new Random();
    }

    private String generate(String[] array) {
        return array[random.nextInt(array.length)];
    }

    public String generateContent() {
        return generate(CONTENTS);
    }
}

package com.herokuapp.erpmesbackend.erpmesbackend.communication.factory;

import java.util.Random;

public class ChannelFactory {

    private final String[] NAMES = {"Zespół pracowników", "Oddział administratorów", "Prywatna konwersacja",
            "Pion finansów", "Dział analityków", "Chat grupowy", "Kanał offtop"};

    private final Random random;

    public ChannelFactory() {
        random = new Random();
    }

    private String generate(String[] array) {
        return array[random.nextInt(array.length)];
    }

    public String generateName() {
        return generate(NAMES);
    }
}

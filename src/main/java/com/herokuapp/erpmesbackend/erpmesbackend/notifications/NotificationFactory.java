package com.herokuapp.erpmesbackend.erpmesbackend.notifications;

import java.util.Random;

public class NotificationFactory {

    private String[] INSTRUCTIONS = {"Uszkodzona przesyłka podczas transportu", "Nieopłacone zamówienie",
            "Brak jednego z przedmiotów do wysłania zamówienia"};

    private final Random random;

    private String generate(String[] array) {
        return array[random.nextInt(array.length)];
    }

    public NotificationFactory() {
        random = new Random();
    }

    private String generateInstruction() {
        return generate(INSTRUCTIONS);
    }

    private State generateState() {
        return State.values()[random.nextInt(State.values().length)];

    }
}

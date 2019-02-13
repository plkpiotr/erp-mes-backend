package com.herokuapp.erpmesbackend.erpmesbackend.communication.factory;

import java.util.Random;

public class NotificationFactory {

    private String[] INSTRUCTIONS = {"Uszkodzona przesyłka", "Nieopłacone zamówienie",
            "Brak jednego z przedmiotów"};

    private String[] DESCRIPTIONS = {"Zadzwonić do odbiorcy", "Wykonać w pierwszej kolejności",
            "Przesłać uwagi przełożonemu"};

    private final Random random;

    private String generate(String[] array) {
        return array[random.nextInt(array.length)];
    }

    public NotificationFactory() {
        random = new Random();
    }

    public String generateInstruction() {
        return generate(INSTRUCTIONS);
    }

    public String generateDescription() {
        return generate(DESCRIPTIONS);
    }
}

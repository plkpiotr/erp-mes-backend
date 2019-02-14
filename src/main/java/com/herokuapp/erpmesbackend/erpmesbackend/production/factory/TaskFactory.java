package com.herokuapp.erpmesbackend.erpmesbackend.production.factory;

import com.herokuapp.erpmesbackend.erpmesbackend.production.model.Type;

import java.time.LocalDateTime;
import java.util.Random;

public class TaskFactory {

    private final String[] NAMES = {"Wysłać przesyłkę #1425", "Wysłać list #1434",
            "Przekazać paczkę #1478", "Zapakować list #1447", "Skompletować paczkę #1442"};

    private final String[] DETAILS = {"Dodać darmowy upominek", "Zmienić sposób dostarczenia na list ekonomiczny",
            "Zmienić sposób dostarczenia na list priorytetowy", "Nakleić informację: \"Uwaga! Szkło\""};

    private final Random random;

    public TaskFactory() {
        random = new Random();
    }

    private String generate(String[] array) {
        return array[random.nextInt(array.length)];
    }

    public String generateName() {
        return generate(NAMES);
    }

    public String generateDetails() {
        return generate(DETAILS);
    }

    public int generateEstimatedTime() {
        return random.nextInt(15) + 5;
    }

    public LocalDateTime generateDeadline() {
        return LocalDateTime.now().plusDays(2);
    }

    public Type generateType() {
        return Type.values()[random.nextInt(Type.values().length)];
    }

    public LocalDateTime generateScheduledTime() {
        return LocalDateTime.now().plusDays(1);
    }
}

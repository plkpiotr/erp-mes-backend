package com.herokuapp.erpmesbackend.erpmesbackend.suggestions;

import java.util.Random;

public class SuggestionFactory {

    private final String[] NAMES = {"Przeprowadzić szkolenie z zakresu umiejętności miękkich",
                                    "Przed następnym podziałem zadań organizować krótkie spotkanie",
                                    "Zmienić liczbę pracowników przy wysyłaniu przesyłek wielkogabarytowych"};

    private final String[] DESCRIPTIONS = {"Propozycja przedstawiana przez wielu współpracowników",
                                           "Zgłoszone po konsultacjach z przełożonym",
                                           "Zaproponowane podczas konferencji"};

    private final Random random;

    public SuggestionFactory() {
        random = new Random();
    }

    private String generate(String[] array) {
        return array[random.nextInt(array.length)];
    }

    public String generateName() {
        return generate(NAMES);
    }

    public String generateDescription() {
        return generate(DESCRIPTIONS);
    }
}

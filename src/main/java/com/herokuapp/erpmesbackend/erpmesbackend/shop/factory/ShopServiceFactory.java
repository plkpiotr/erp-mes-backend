package com.herokuapp.erpmesbackend.erpmesbackend.shop.factory;

import com.herokuapp.erpmesbackend.erpmesbackend.shop.model.Resolution;

import java.util.Random;

public class ShopServiceFactory {

    private final String[] FIRST_NAMES = {"Piotr", "Jan", "Beniamin", "Anna", "Maria"};

    private final String[] LAST_NAMES = {"Woźniak", "Krawczyk", "Zając", "Król", "Szewczyk"};

    private final String[] EMAILS = {"firma@firma.pl", "przedsiebiorstwo@przedsiebiorstwo.pl", "business@business.pl",
            "uczelnia@uczelnia.pl", "hurtownia@hurtownia.pl"};

    private final String[] PHONE_NUMBERS = {"625875322", "158643422", "567154333", "312312800", "321212890"};

    private final String[] STREETS = {"ul. Witolda Pileckiego", "ul. Krakowska", "ul. Zbigniewa Herberta",
            "ul. 11 Listopada", "ul. Tadeusza Gajcego"};

    private final String[] HOUSE_NUMBERS = {"11/16", "36", "239b", "8e", "16b"};

    private final String[] CITIES = {"Nisko", "Zakopane", "Tarnobrzeg", "Warszawa", "Tuchola"};

    private final String[] POSTAL_CODES = {"96-111", "44-144", "77-140", "11-997", "50-001"};

    private final Random random;

    public ShopServiceFactory() {
        random = new Random();
    }

    private String generate(String[] array) {
        return array[random.nextInt(array.length)];
    }

    public String generateFirstName() {
        return generate(FIRST_NAMES);
    }

    public String generateLastName() {
        return generate(LAST_NAMES);
    }

    public String generateEmail() {
        return generate(EMAILS);
    }

    public String generatePhoneNumber() {
        return generate(PHONE_NUMBERS);
    }

    public String generateStreet() {
        return generate(STREETS);
    }

    public String generateHouseNumber() {
        return generate(HOUSE_NUMBERS);
    }

    public String generateCity() {
        return generate(CITIES);
    }

    public String generatePostalCode() {
        return generate(POSTAL_CODES);
    }

    public Resolution generateResolution() {
        return Resolution.values()[random.nextInt(Resolution.values().length)];
    }
}

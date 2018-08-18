package com.herokuapp.erpmesbackend.erpmesbackend.warehouses;

import com.herokuapp.erpmesbackend.erpmesbackend.orders.Status;

import java.util.Random;

public class WarehouseFactory {

    private final String[] STREETS = {"ul. Łukasza Cieplińskiego", "ul. Wileńska", "ul. Artura Grottgera",
        "ul. Lwowska", "ul. Henryka Sienkiewicza"};

    private final String[] HOUSE_NUMBERS = {"16/14", "38", "225a", "313d", "16b"};

    private final String[] CITIES = {"Stalowa Wola", "Piotrków Trybunalski", "Ustka", "Przemyśl", "Krzeszów"};

    private final String[] POSTAL_CODES = {"37-418", "88-313", "24-140", "16-107", "96-004"};

    private final Random random;

    public WarehouseFactory() {
        random = new Random();
    }

    private String generate(String[] array) {
        return array[random.nextInt(array.length)];
    }

    public Status generateStatus() {
        return Status.values()[random.nextInt(Status.values().length)];
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
}

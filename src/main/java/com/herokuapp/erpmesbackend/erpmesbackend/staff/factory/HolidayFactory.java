package com.herokuapp.erpmesbackend.erpmesbackend.staff.factory;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.request.HolidayRequest;
import com.herokuapp.erpmesbackend.erpmesbackend.staff.model.HolidayType;

import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class HolidayFactory {

    private Random r;

    public HolidayFactory() {
        r = new Random();
    }

    private LocalDate generateStartDate() {
        long today = LocalDate.now().toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(today + 1, today + 20);
        return LocalDate.ofEpochDay(randomDay);
    }

    private int generateDuration() {
        return r.nextInt(5) + 1;
    }

    public HolidayRequest generateHolidayRequest() {
        return new HolidayRequest(generateStartDate(), generateDuration(), HolidayType.VACATION);
    }

    public HolidayRequest generateHolidayRequestWithType(HolidayType type) {
        return new HolidayRequest(generateStartDate(), generateDuration(), type);
    }
}

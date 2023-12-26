package com.maiia.pro.entity;

import com.maiia.pro.EntityFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class HasDurationTest {

    private final  EntityFactory entityFactory = new EntityFactory();
    private  final static Integer patient_id = 657679;

    @Test()
    void startBeforeEndAtSameTime() {
        Practitioner practitioner = entityFactory.createPractitioner();
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

        Appointment a = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45));

        HasDuration b = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(15),
                startDate.plusMinutes(30));

        assertEquals(a.atTheSameTime(b), false);
    }

    @Test()
    void startAfterEndAter() {
        Practitioner practitioner = entityFactory.createPractitioner();
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

        Appointment a = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45));

        HasDuration b = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(45),
                startDate.plusMinutes(60));

        assertEquals(a.atTheSameTime(b), false);
    }

    @Test()
    void atSameTime() {

        Practitioner practitioner = entityFactory.createPractitioner();
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

        Appointment a = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45));

        HasDuration b = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45));

        assertEquals(a.atTheSameTime(b), true);
    }

    @Test()
    void startBeforeEndDuring() {

        Practitioner practitioner = entityFactory.createPractitioner();
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

        Appointment a = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45));

        HasDuration b = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(25),
                startDate.plusMinutes(35));

        assertEquals(a.atTheSameTime(b), true);
    }

    @Test()
    void startSameTimeEndBefore() {

        Practitioner practitioner = entityFactory.createPractitioner();
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

        Appointment a = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45));

        HasDuration b = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(35));

        assertEquals(a.atTheSameTime(b), true);
    }

    @Test()
    void startAfterEndSameTime() {

        Practitioner practitioner = entityFactory.createPractitioner();
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

        Appointment a = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45));

        HasDuration b = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(40),
                startDate.plusMinutes(45));

        assertEquals(a.atTheSameTime(b), true);
    }

    @Test()
    void startAfterEnDAfter() {

        Practitioner practitioner = entityFactory.createPractitioner();
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);

        Appointment a = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45));

        HasDuration b = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(40),
                startDate.plusMinutes(50));

        assertEquals(a.atTheSameTime(b), true);
    }
    
}

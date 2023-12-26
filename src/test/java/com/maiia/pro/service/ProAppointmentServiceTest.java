package com.maiia.pro.service;

import com.maiia.pro.EntityFactory;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.entity.Practitioner;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.AvailabilityRepository;
import com.maiia.pro.repository.PractitionerRepository;
import com.maiia.pro.repository.TimeSlotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProAppointmentServiceTest {

    private final  EntityFactory entityFactory = new EntityFactory();
    private  final static Integer patient_id=657679;

    @Autowired
    private ProAppointmentService proAppointmentService;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Test
    void createAppointment() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));

        Appointment appointment = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45));

        proAppointmentService.create(appointment);

        List<Appointment> appointments = appointmentRepository.findByPractitionerId(practitioner.getId());

        assertEquals(1, appointments.size());
    }

    @Test()
    void createAppointmentOnSameSlot() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45)));

        Appointment appointment = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proAppointmentService.create(appointment);
        });

        String expectedMessage = "Appointement for this practitioner already exists at that time !";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test()
    void createAppointmentOverlappingAfter() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45)));

        Appointment appointment = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(40),
                startDate.plusMinutes(50));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proAppointmentService.create(appointment);
        });

        String expectedMessage = "Appointement for this practitioner already exists at that time !";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test()
    void createAppointmentOverlappingBefore() {
        Practitioner practitioner = practitionerRepository.save(entityFactory.createPractitioner());
        LocalDateTime startDate = LocalDateTime.of(2020, Month.FEBRUARY, 5, 11, 0, 0);
        timeSlotRepository.save(entityFactory.createTimeSlot(practitioner.getId(), startDate, startDate.plusHours(1)));

        appointmentRepository.save(entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(30),
                startDate.plusMinutes(45)));

        Appointment appointment = entityFactory.createAppointment(practitioner.getId(),
                patient_id,
                startDate.plusMinutes(25),
                startDate.plusMinutes(35));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            proAppointmentService.create(appointment);
        });

        String expectedMessage = "Appointement for this practitioner already exists at that time !";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    
}

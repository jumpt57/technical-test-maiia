package com.maiia.pro.service;

import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Availability;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.AvailabilityRepository;
import com.maiia.pro.repository.TimeSlotRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.time.temporal.ChronoUnit;


import java.time.LocalDateTime;

@Service
public class ProAvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    public List<Availability> findByPractitionerId(Integer practitionerId) {
        return availabilityRepository.findByPractitionerId(practitionerId);
    }

    public List<Availability> generateAvailabilities(Integer practitionerId) {

        List<TimeSlot> timeSlots = timeSlotRepository.findByPractitionerId(practitionerId);
        List<Appointment> appointments = appointmentRepository.findByPractitionerId(practitionerId);
        
        List<Availability> availabilities = timeSlots.stream()
            .flatMap(timeSlot -> generateAvailability(appointments, timeSlot).stream())
            .collect(Collectors.toList());

        availabilityRepository.deleteAll();

        return StreamSupport.stream(availabilityRepository.saveAll(availabilities).spliterator(), false)
            .collect(Collectors.toList());
    }

    private List<Availability> generateAvailability(List<Appointment> appointments, TimeSlot timeSlot) {
        List<Availability> availabilities = new ArrayList<>();
        LocalDateTime nextTimeSlot = timeSlot.getStartDate();

        int limit = 96; // Hard limit to avoid infinite loop 96 is the number of 15 minutes slots in one day
        
        while (nextTimeSlot.isBefore(timeSlot.getEndDate()) && limit != 0) {

            Availability newAvailability = Availability.builder()
                    .practitionerId(timeSlot.getPractitionerId())
                    .startDate(nextTimeSlot)
                    .endDate(nextTimeSlot.plus(15, ChronoUnit.MINUTES))
                    .build();

            Optional<LocalDateTime> appointmentEndDate = findAppointmentAtSameTime(appointments, newAvailability);
            LocalDateTime definitiveEndDate = appointmentEndDate.orElse(newAvailability.getEndDate());

            newAvailability.setEndDate(definitiveEndDate);

            if (appointmentEndDate.isEmpty()) {
                availabilities.add(newAvailability);
            }

            nextTimeSlot = definitiveEndDate;

            limit--;
        }

        return availabilities;
    }

    private Optional<LocalDateTime> findAppointmentAtSameTime(List<Appointment> currentAppointments, Availability availibility) {
        return currentAppointments.stream()
                .filter(appointment -> appointment.atTheSameTime(availibility))
                .map(Appointment::getEndDate)
                .findFirst();
    }

}

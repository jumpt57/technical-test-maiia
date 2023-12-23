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

    private List<Availability> generateAvailability(List<Appointment> currentAppointments, TimeSlot timeSlot) {
        List<Availability> availabilities = new ArrayList<>();
        LocalDateTime nextTimeSlot = timeSlot.getStartDate();

        int limit = 96; // Hard limit to avoid infinite loop 96 is the number of 15 minutes slots in one day
        
        while (nextTimeSlot.isBefore(timeSlot.getEndDate()) && limit != 0) {

            Optional<LocalDateTime> appointmentEndDate = findAppointmentStartAtSameTime(currentAppointments, nextTimeSlot);
            var endDate = appointmentEndDate.orElse(nextTimeSlot.plus(15, ChronoUnit.MINUTES));

            if (appointmentEndDate.isEmpty()) {
                 Availability availability = Availability.builder()
                    .practitionerId(timeSlot.getPractitionerId())
                    .startDate(nextTimeSlot)
                    .endDate(endDate)
                    .build();

                availabilities.add(availability);
            }

            nextTimeSlot = endDate;

            limit--;
        }

        return availabilities;
    }

    private Optional<LocalDateTime> findAppointmentStartAtSameTime(List<Appointment> currentAppointments, LocalDateTime availibilityStartTime) {
        return currentAppointments.stream()
                .filter(appointment -> notAtSameTime(appointment, availibilityStartTime))
                .map(Appointment::getEndDate)
                .findFirst();
    }

    private Boolean notAtSameTime(Appointment appointment,  LocalDateTime availibilityStartTime) { 
        var availabilityEndDate = availibilityStartTime.plus(15, ChronoUnit.MINUTES);
        return !((availibilityStartTime.isBefore(appointment.getStartDate()) && (availabilityEndDate.isBefore(appointment.getStartDate()) || availabilityEndDate.isEqual(appointment.getStartDate())) ) ||
            ((availibilityStartTime.isAfter(appointment.getEndDate()) || availibilityStartTime.isEqual(appointment.getEndDate())) && availabilityEndDate.isAfter(appointment.getEndDate())));
    }

}

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
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.same;

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
        List<Availability> currentAvailabilities = availabilityRepository.findByPractitionerId(practitionerId);
        List<Appointment> currentAppointments = appointmentRepository.findByPractitionerId(practitionerId);

        List<Availability> validatedAvailabilities = currentAvailabilities.stream()
            .filter(availability -> !malformed(availability))
            .collect(Collectors.toList());

        currentAvailabilities.stream()
            .filter(this::malformed)
            .forEach(availabilityRepository::delete);

        return timeSlots.stream()
            .flatMap(timeSlot -> generateAvailability(currentAppointments, timeSlot).stream())
            .filter(availability -> unique(validatedAvailabilities, availability))
            .map(availabilityRepository::save)
            .collect(Collectors.toList());
    }

    private List<Availability> generateAvailability(List<Appointment> currentAppointments, TimeSlot timeSlot) {
        List<Availability> availabilities = new ArrayList<>();
        LocalDateTime nextTimeSlot = timeSlot.getStartDate();

        int maxRun = 999;
        
        while (nextTimeSlot.isBefore(timeSlot.getEndDate()) && maxRun != 0) {

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

            maxRun--;
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


    private Boolean unique(List<Availability> currentAvailabilities, Availability newAvailibility) {
        return !currentAvailabilities.stream()
                .anyMatch(availibility -> availibility.getStartDate().isEqual(newAvailibility.getStartDate()) && availibility.getEndDate().isEqual(newAvailibility.getEndDate()));
    }

    private Boolean malformed(Availability availability) {
        return !availability.getStartDate().isBefore(availability.getEndDate());
    }

}

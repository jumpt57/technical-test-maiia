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
        List<Availability> currentAvailabilities = availabilityRepository.findByPractitionerId(practitionerId);

        return timeSlots.stream()
            .flatMap(timeSlot -> generateIntervals(timeSlot).stream())
            .filter(newAvailibility -> !currentAvailabilities.stream()
                .anyMatch(availibility -> availibility.getStartDate().isEqual(newAvailibility.getStartDate()) && availibility.getEndDate().isEqual(newAvailibility.getEndDate())))
            .map(newAvailibility -> availabilityRepository.save(newAvailibility))
            .collect(Collectors.toList());
    }

    private List<Availability> generateIntervals(TimeSlot timeSlot) {
        List<Availability> availabilities = new ArrayList<>();
        LocalDateTime currentTimeSlot = timeSlot.getStartDate();
        List<Appointment> currentAppointments = appointmentRepository.findByPractitionerId(timeSlot.getPractitionerId());

        while (currentTimeSlot.isBefore(timeSlot.getEndDate())) {

           LocalDateTime newEndDate = currentAppointments.stream()
                .filter(appointment -> atTheSameTime(appointment, timeSlot))
                .map(appointment -> appointment.getEndDate())
                .findFirst()
                .orElse(currentTimeSlot.plus(15, ChronoUnit.MINUTES));

            Availability availability = Availability.builder()
                .practitionerId(timeSlot.getPractitionerId())
                .startDate(currentTimeSlot)
                .endDate(newEndDate)
                .build();

            availabilities.add(availability);

            currentTimeSlot = newEndDate;
        }

        return availabilities;
    }

    private Boolean atTheSameTime(Appointment appointment, TimeSlot timeSlot) {
        
        if(timeSlot.getStartDate().isEqual(appointment.getStartDate())) {
            return true;
        } else if(timeSlot.getStartDate().isAfter(appointment.getStartDate()) && timeSlot.getStartDate().isBefore(appointment.getEndDate())) {
            return true;
        } else {
            return false;
        }
    }
}

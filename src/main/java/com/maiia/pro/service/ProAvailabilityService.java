package com.maiia.pro.service;

import com.maiia.pro.entity.Availability;
import com.maiia.pro.entity.TimeSlot;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.AvailabilityRepository;
import com.maiia.pro.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

        return timeSlots.stream()
            .flatMap(timeSlot -> generateIntervals(timeSlot).stream())
            .map(availability -> availabilityRepository.save(availability))
            .collect(Collectors.toList());
    }

    private List<Availability> generateIntervals(TimeSlot timeSlot) {
        List<Availability> availabilities = new ArrayList<>();
        LocalDateTime current = timeSlot.getStartDate();

        while (current.isBefore(timeSlot.getEndDate())) {

            LocalDateTime newEndDate = current.plus(15, ChronoUnit.MINUTES);

            Availability availability = Availability.builder()
                .practitionerId(timeSlot.getPractitionerId())
                .startDate(current)
                .endDate(newEndDate)
                .build();

            availabilities.add(availability);

            current = newEndDate;
        }

        return availabilities;
    }
}

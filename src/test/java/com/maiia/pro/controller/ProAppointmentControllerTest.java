package com.maiia.pro.controller;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maiia.pro.EntityFactory;
import com.maiia.pro.ProApplication;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.entity.Practitioner;
import com.maiia.pro.repository.AppointmentRepository;
import com.maiia.pro.repository.PractitionerRepository;
import com.maiia.pro.repository.TimeSlotRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = ProApplication.class)
@AutoConfigureMockMvc
public class ProAppointmentControllerTest {

    private final  EntityFactory entityFactory = new EntityFactory();
    private  final static Integer patient_id=657679;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Test
    public void createSecondAppointAtSameTimeThenBadRequest() throws Exception {

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


        mvc.perform(post("/appointments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(appointment)))
            .andExpect(status().isBadRequest());
    }
    
}



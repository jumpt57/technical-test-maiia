package com.maiia.pro.controller;

import com.maiia.pro.dto.AppointmentDto;
import com.maiia.pro.entity.Appointment;
import com.maiia.pro.service.ProAppointmentService;
import io.swagger.annotations.ApiOperation;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/appointments", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProAppointmentController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProAppointmentService proAppointmentService;

    @ApiOperation(value = "Get appointments by practitionerId")
    @GetMapping("/{practitionerId}")
    public List<Appointment> getAppointmentsByPractitioner(@PathVariable final Integer practitionerId) {
        return proAppointmentService.findByPractitionerId(practitionerId);
    }

    @ApiOperation(value = "Get all appointments")
    @GetMapping
    public List<Appointment> getAppointments() {
        return proAppointmentService.findAll();
    }

    @PostMapping
    public Appointment create(@RequestBody AppointmentDto appointmentDto) {
        try {
            return proAppointmentService.create(modelMapper.map(appointmentDto, Appointment.class));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}

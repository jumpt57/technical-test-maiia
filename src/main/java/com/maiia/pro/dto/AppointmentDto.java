package com.maiia.pro.dto;

import java.time.LocalDateTime;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

    @NonNull
    private Integer patientId;

    @NonNull
    private Integer practitionerId;

    @NonNull
    private LocalDateTime startDate;

    @NonNull
    private LocalDateTime endDate;
    
}

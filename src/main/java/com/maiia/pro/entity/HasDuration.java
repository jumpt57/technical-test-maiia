package com.maiia.pro.entity;

import java.time.LocalDateTime;

public interface HasDuration {

    public LocalDateTime getStartDate();

    public LocalDateTime getEndDate();

    default Boolean atTheSameTime(HasDuration comparee) {
        return comparee.getEndDate().isAfter(getStartDate()) && comparee.getEndDate().isBefore(getEndDate()) || 
            comparee.getStartDate().isAfter(getStartDate()) && comparee.getStartDate().isBefore(getEndDate()) || 
            comparee.getStartDate().isEqual(getStartDate()) && comparee.getEndDate().isEqual(getEndDate());
    }

} 
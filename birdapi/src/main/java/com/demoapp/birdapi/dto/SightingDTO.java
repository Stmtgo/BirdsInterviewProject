package com.demoapp.birdapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class SightingDTO {

    private Long id;

    @NotNull(message = "Bird ID is required")
    private Long birdId;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Date and time is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTime;

    // Bird details for response
    private BirdDTO bird;

    public SightingDTO() {
    }

    public SightingDTO(Long id, Long birdId, String location, LocalDateTime dateTime) {
        this.id = id;
        this.birdId = birdId;
        this.location = location;
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBirdId() {
        return birdId;
    }

    public void setBirdId(Long birdId) {
        this.birdId = birdId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public BirdDTO getBird() {
        return bird;
    }

    public void setBird(BirdDTO bird) {
        this.bird = bird;
    }
}


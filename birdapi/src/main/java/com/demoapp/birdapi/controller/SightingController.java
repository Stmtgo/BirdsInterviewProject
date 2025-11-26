package com.demoapp.birdapi.controller;

import com.demoapp.birdapi.dto.SightingDTO;
import com.demoapp.birdapi.service.SightingService;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sightings")
public class SightingController {

    private final SightingService sightingService;

    public SightingController(SightingService sightingService) {
        this.sightingService = sightingService;
    }

    @PostMapping
    public ResponseEntity<SightingDTO> createSighting(@Valid @RequestBody SightingDTO sightingDTO) {
        SightingDTO createdSighting = sightingService.createSighting(sightingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSighting);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SightingDTO> updateSighting(
            @PathVariable Long id,
            @Valid @RequestBody SightingDTO sightingDTO) {
        SightingDTO updatedSighting = sightingService.updateSighting(id, sightingDTO);
        return ResponseEntity.ok(updatedSighting);
    }

    @GetMapping
    public Page<SightingDTO> getAllSightings(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return sightingService.getAllSightings(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SightingDTO> getSightingById(@PathVariable Long id) {
        SightingDTO sighting = sightingService.getSightingById(id);
        return ResponseEntity.ok(sighting);
    }

    @GetMapping("/search")
    public Page<SightingDTO> searchSighting(@RequestParam(required = false) String birdName,
                                            @RequestParam(required = false) String location,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate,
                                            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return sightingService.searchSightings(birdName, location, fromDate, toDate, pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSighting(@PathVariable Long id) {
        sightingService.deleteSighting(id);
        return ResponseEntity.noContent().build();
    }
}


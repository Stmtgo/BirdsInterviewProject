package com.demoapp.birdapi.controller;

import com.demoapp.birdapi.dto.BirdDTO;
import com.demoapp.birdapi.service.BirdService;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/birds")
public class BirdController {

    private final BirdService birdService;

    public BirdController(BirdService birdService) {
        this.birdService = birdService;
    }

    @PostMapping
    public ResponseEntity<BirdDTO> createBird(@Valid @RequestBody BirdDTO birdDTO) {
        BirdDTO createdBird = birdService.createBird(birdDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBird);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BirdDTO> updateBird(
            @PathVariable Long id,
            @Valid @RequestBody BirdDTO birdDTO) {
        BirdDTO updatedBird = birdService.updateBird(id, birdDTO);
        return ResponseEntity.ok(updatedBird);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBird(@PathVariable Long id) {
        birdService.deleteBird(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BirdDTO> getBirdById(@PathVariable Long id) {
        BirdDTO bird = birdService.getBirdById(id);
        return ResponseEntity.ok(bird);
    }

    @GetMapping
    public Page<BirdDTO> getAllBirds(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return birdService.getAllBirds(pageable);
    }

    @GetMapping("/search")
    public Page<BirdDTO> searchBird(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color,
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return birdService.searchBirdsByNameAndColor(name, color, pageable);
    }

}


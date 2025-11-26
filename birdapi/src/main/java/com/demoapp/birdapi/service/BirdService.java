package com.demoapp.birdapi.service;

import com.demoapp.birdapi.controller.BirdController;
import com.demoapp.birdapi.dto.BirdDTO;
import com.demoapp.birdapi.exception.ResourceNotFoundException;
import com.demoapp.birdapi.mapper.BirdMapper;
import com.demoapp.birdapi.model.Bird;
import com.demoapp.birdapi.repository.BirdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BirdService {

    private static final Logger log = LoggerFactory.getLogger(BirdService.class);

    private final BirdRepository birdRepository;
    private final BirdMapper birdMapper;

    public BirdService(BirdRepository birdRepository, BirdMapper birdMapper) {
        this.birdRepository = birdRepository;
        this.birdMapper = birdMapper;
    }

    public BirdDTO createBird(BirdDTO birdDTO) {
        log.info("Creating bird: {}", birdDTO);

        Bird bird = birdMapper.toEntity(birdDTO);
        Bird savedBird = birdRepository.save(bird);
        return birdMapper.toDTO(savedBird);
    }

    public BirdDTO updateBird(Long id, BirdDTO birdDTO) {
        log.info("Updating bird id={} payload={}", id, birdDTO);

        Bird bird = birdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bird", id));

        birdMapper.updateEntityFromDTO(birdDTO, bird);
        Bird updatedBird = birdRepository.save(bird);
        return birdMapper.toDTO(updatedBird);
    }

    public BirdDTO getBirdById(Long id) {
        log.info("Retrieving bird id={}", id);

        Bird bird = birdRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bird", id));
        return birdMapper.toDTO(bird);
    }

    public Page<BirdDTO> getAllBirds(Pageable pageable) {
        log.info("Listing all birds, pageable={}", pageable);

        return birdRepository.findAll(pageable)
                .map(birdMapper::toDTO);
    }

    public void deleteBird(Long id) {
        log.info("Deleting bird id={}", id);

        if (!birdRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bird", id);
        }
        birdRepository.deleteById(id);
    }

    public Page<BirdDTO> searchBirdsByNameAndColor(String name, String color, Pageable pageable) {
        log.info("Searching birds name={} color={} pageable={}", name, color, pageable);

        return birdRepository.findByNameContainingIgnoreCaseAndColorIgnoreCase(name, color, pageable)
                .map(birdMapper::toDTO);
    }

}


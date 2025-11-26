package com.demoapp.birdapi.service;

import com.demoapp.birdapi.dto.SightingDTO;
import com.demoapp.birdapi.exception.ResourceNotFoundException;
import com.demoapp.birdapi.mapper.SightingMapper;
import com.demoapp.birdapi.model.Bird;
import com.demoapp.birdapi.model.Sighting;
import com.demoapp.birdapi.repository.BirdRepository;
import com.demoapp.birdapi.repository.SightingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SightingServiceTest {

    @Mock
    private SightingRepository sightingRepository;

    @Mock
    private BirdRepository birdRepository;

    @Mock
    private SightingMapper sightingMapper;

    @InjectMocks
    private SightingService sightingService;

    private Bird bird;
    private Sighting sighting;
    private SightingDTO sightingDTO;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testDateTime = LocalDateTime.of(2025, 1, 15, 10, 30);

        bird = new Bird("Sparrow", "Brown", 10.0, 5.0);
        bird.setId(1L);

        sighting = new Sighting();
        sighting.setId(100L);
        sighting.setBird(bird);
        sighting.setLocation("Central Park");
        sighting.setDateTime(testDateTime);

        sightingDTO = new SightingDTO();
        sightingDTO.setId(100L);
        sightingDTO.setBirdId(1L);
        sightingDTO.setLocation("Central Park");
        sightingDTO.setDateTime(testDateTime);
    }

    @Test
    void getAllSightings_shouldReturnPageOfDtos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        Page<Sighting> page = new PageImpl<>(Collections.singletonList(sighting), pageable, 1);
        when(sightingRepository.findAll(pageable)).thenReturn(page);
        when(sightingMapper.toDTO(sighting)).thenReturn(sightingDTO);

        // Act
        Page<SightingDTO> result = sightingService.getAllSightings(pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Central Park", result.getContent().get(0).getLocation());
        verify(sightingRepository).findAll(pageable);
        verify(sightingMapper).toDTO(sighting);
    }

    @Test
    void getSightingById_whenFound_shouldReturnDto() {
        // Arrange
        when(sightingRepository.findById(100L)).thenReturn(Optional.of(sighting));
        when(sightingMapper.toDTO(sighting)).thenReturn(sightingDTO);

        // Act
        SightingDTO result = sightingService.getSightingById(100L);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Central Park", result.getLocation());
        verify(sightingRepository).findById(100L);
        verify(sightingMapper).toDTO(sighting);
    }

    @Test
    void getSightingById_whenNotFound_shouldThrowException() {
        // Arrange
        when(sightingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> sightingService.getSightingById(999L));
        verify(sightingRepository).findById(999L);
        verify(sightingMapper, never()).toDTO(any());
    }

    @Test
    void createSighting_whenBirdExists_shouldPersistAndReturnDto() {
        // Arrange
        when(birdRepository.findById(1L)).thenReturn(Optional.of(bird));
        when(sightingRepository.save(any(Sighting.class))).thenReturn(sighting);
        when(sightingMapper.toDTO(sighting)).thenReturn(sightingDTO);

        // Act
        SightingDTO result = sightingService.createSighting(sightingDTO);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Central Park", result.getLocation());

        ArgumentCaptor<Sighting> captor = ArgumentCaptor.forClass(Sighting.class);
        verify(sightingRepository).save(captor.capture());
        Sighting saved = captor.getValue();
        assertEquals("Central Park", saved.getLocation());
        assertEquals(bird, saved.getBird());
        assertEquals(testDateTime, saved.getDateTime());
    }

    @Test
    void createSighting_whenBirdNotFound_shouldThrowException() {
        // Arrange
        when(birdRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> sightingService.createSighting(sightingDTO));
        verify(birdRepository).findById(1L);
        verify(sightingRepository, never()).save(any());
    }

    @Test
    void updateSighting_whenFound_shouldUpdateAndReturnDto() {
        // Arrange
        SightingDTO updateDTO = new SightingDTO();
        updateDTO.setBirdId(1L);
        updateDTO.setLocation("New Location");
        updateDTO.setDateTime(testDateTime.plusDays(1));

        when(sightingRepository.findById(100L)).thenReturn(Optional.of(sighting));
        when(birdRepository.findById(1L)).thenReturn(Optional.of(bird));
        when(sightingRepository.save(any(Sighting.class))).thenReturn(sighting);
        when(sightingMapper.toDTO(sighting)).thenReturn(sightingDTO);

        // Act
        SightingDTO result = sightingService.updateSighting(100L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(sightingRepository).findById(100L);
        verify(birdRepository).findById(1L);
        verify(sightingRepository).save(sighting);
    }

    @Test
    void updateSighting_whenSightingNotFound_shouldThrowException() {
        // Arrange
        when(sightingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> sightingService.updateSighting(999L, sightingDTO));
        verify(sightingRepository).findById(999L);
        verify(sightingRepository, never()).save(any());
    }

    @Test
    void updateSighting_whenBirdNotFound_shouldThrowException() {
        // Arrange
        when(sightingRepository.findById(100L)).thenReturn(Optional.of(sighting));
        when(birdRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> sightingService.updateSighting(100L, sightingDTO));
        verify(sightingRepository).findById(100L);
        verify(birdRepository).findById(1L);
        verify(sightingRepository, never()).save(any());
    }

    @Test
    void deleteSighting_whenExists_shouldDelete() {
        // Arrange
        when(sightingRepository.existsById(100L)).thenReturn(true);

        // Act
        sightingService.deleteSighting(100L);

        // Assert
        verify(sightingRepository).existsById(100L);
        verify(sightingRepository).deleteById(100L);
    }

    @Test
    void deleteSighting_whenNotExists_shouldThrowException() {
        // Arrange
        when(sightingRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> sightingService.deleteSighting(999L));
        verify(sightingRepository).existsById(999L);
        verify(sightingRepository, never()).deleteById(anyLong());
    }

    @Test
    void searchSightings_withAllParameters_shouldReturnPageOfDtos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        Page<Sighting> page = new PageImpl<>(Collections.singletonList(sighting), pageable, 1);
        LocalDateTime fromDate = testDateTime.minusDays(1);
        LocalDateTime toDate = testDateTime.plusDays(1);

        when(sightingRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(sightingMapper.toDTO(sighting)).thenReturn(sightingDTO);

        // Act
        Page<SightingDTO> result = sightingService.searchSightings("Sparrow", "Park", fromDate, toDate, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Central Park", result.getContent().get(0).getLocation());
        verify(sightingRepository).findAll(any(Specification.class), eq(pageable));
        verify(sightingMapper).toDTO(sighting);
    }

    @Test
    void searchSightings_withNoParameters_shouldReturnAll() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        Page<Sighting> page = new PageImpl<>(Collections.singletonList(sighting), pageable, 1);

        when(sightingRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(sightingMapper.toDTO(sighting)).thenReturn(sightingDTO);

        // Act
        Page<SightingDTO> result = sightingService.searchSightings(null, null, null, null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(sightingRepository).findAll(any(Specification.class), eq(pageable));
    }
}


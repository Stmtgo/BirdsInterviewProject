package com.demoapp.birdapi.service;

import com.demoapp.birdapi.dto.BirdDTO;
import com.demoapp.birdapi.exception.ResourceNotFoundException;
import com.demoapp.birdapi.mapper.BirdMapper;
import com.demoapp.birdapi.model.Bird;
import com.demoapp.birdapi.repository.BirdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BirdServiceTest {

    @Mock
    private BirdRepository birdRepository;

    @Mock
    private BirdMapper birdMapper;

    @InjectMocks
    private BirdService birdService;

    private Bird bird;
    private BirdDTO birdDTO;

    @BeforeEach
    void setUp() {
        bird = new Bird("Sparrow", "Brown", 10.5, 12.0);
        bird.setId(1L);

        birdDTO = new BirdDTO();
        birdDTO.setId(1L);
        birdDTO.setName("Sparrow");
        birdDTO.setColor("Brown");
        birdDTO.setWeight(10.5);
        birdDTO.setHeight(12.0);
    }

    @Test
    void createBird_shouldPersistAndReturnDto() {
        // Arrange
        when(birdMapper.toEntity(birdDTO)).thenReturn(bird);
        when(birdRepository.save(bird)).thenReturn(bird);
        when(birdMapper.toDTO(bird)).thenReturn(birdDTO);

        // Act
        BirdDTO result = birdService.createBird(birdDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Sparrow", result.getName());
        assertEquals("Brown", result.getColor());
        verify(birdMapper).toEntity(birdDTO);
        verify(birdRepository).save(bird);
        verify(birdMapper).toDTO(bird);
    }

    @Test
    void getBirdById_whenFound_shouldReturnDto() {
        // Arrange
        when(birdRepository.findById(1L)).thenReturn(Optional.of(bird));
        when(birdMapper.toDTO(bird)).thenReturn(birdDTO);

        // Act
        BirdDTO result = birdService.getBirdById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Sparrow", result.getName());
        verify(birdRepository).findById(1L);
        verify(birdMapper).toDTO(bird);
    }

    @Test
    void getBirdById_whenNotFound_shouldThrowException() {
        // Arrange
        when(birdRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> birdService.getBirdById(999L));
        verify(birdRepository).findById(999L);
        verify(birdMapper, never()).toDTO(any());
    }

    @Test
    void updateBird_whenFound_shouldUpdateAndReturnDto() {
        // Arrange
        when(birdRepository.findById(1L)).thenReturn(Optional.of(bird));
        doNothing().when(birdMapper).updateEntityFromDTO(birdDTO, bird);
        when(birdRepository.save(bird)).thenReturn(bird);
        when(birdMapper.toDTO(bird)).thenReturn(birdDTO);

        // Act
        BirdDTO result = birdService.updateBird(1L, birdDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(birdRepository).findById(1L);
        verify(birdMapper).updateEntityFromDTO(birdDTO, bird);
        verify(birdRepository).save(bird);
        verify(birdMapper).toDTO(bird);
    }

    @Test
    void updateBird_whenNotFound_shouldThrowException() {
        // Arrange
        when(birdRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> birdService.updateBird(999L, birdDTO));
        verify(birdRepository).findById(999L);
        verify(birdRepository, never()).save(any());
    }

    @Test
    void deleteBird_whenExists_shouldDelete() {
        // Arrange
        when(birdRepository.existsById(1L)).thenReturn(true);

        // Act
        birdService.deleteBird(1L);

        // Assert
        verify(birdRepository).existsById(1L);
        verify(birdRepository).deleteById(1L);
    }

    @Test
    void deleteBird_whenNotExists_shouldThrowException() {
        // Arrange
        when(birdRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> birdService.deleteBird(999L));
        verify(birdRepository).existsById(999L);
        verify(birdRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllBirds_shouldReturnPageOfDtos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        Page<Bird> birdPage = new PageImpl<>(Collections.singletonList(bird), pageable, 1);
        when(birdRepository.findAll(pageable)).thenReturn(birdPage);
        when(birdMapper.toDTO(bird)).thenReturn(birdDTO);

        // Act
        Page<BirdDTO> result = birdService.getAllBirds(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Sparrow", result.getContent().get(0).getName());
        verify(birdRepository).findAll(pageable);
        verify(birdMapper).toDTO(bird);
    }

    @Test
    void searchBirdsByNameAndColor_shouldReturnPageOfMatchingDtos() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 5);
        Page<Bird> birdPage = new PageImpl<>(Collections.singletonList(bird), pageable, 1);
        when(birdRepository.findByNameContainingIgnoreCaseAndColorIgnoreCase("Sparrow", "Brown", pageable))
                .thenReturn(birdPage);
        when(birdMapper.toDTO(bird)).thenReturn(birdDTO);

        // Act
        Page<BirdDTO> result = birdService.searchBirdsByNameAndColor("Sparrow", "Brown", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Sparrow", result.getContent().get(0).getName());
        assertEquals("Brown", result.getContent().get(0).getColor());
        verify(birdRepository).findByNameContainingIgnoreCaseAndColorIgnoreCase("Sparrow", "Brown", pageable);
        verify(birdMapper).toDTO(bird);
    }
}


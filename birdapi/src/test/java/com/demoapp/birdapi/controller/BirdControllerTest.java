package com.demoapp.birdapi.controller;

import com.demoapp.birdapi.dto.BirdDTO;
import com.demoapp.birdapi.exception.ResourceNotFoundException;
import com.demoapp.birdapi.service.BirdService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BirdController.class)
class BirdControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BirdService birdService;

    @Test
    void createBird_withValidData_shouldReturn201() throws Exception {
        // Arrange
        BirdDTO requestDTO = new BirdDTO(null, "Sparrow", "Brown", 10.5, 12.0);
        BirdDTO responseDTO = new BirdDTO(1L, "Sparrow", "Brown", 10.5, 12.0);

        when(birdService.createBird(any(BirdDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/birds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Sparrow")))
                .andExpect(jsonPath("$.color", is("Brown")))
                .andExpect(jsonPath("$.weight", is(10.5)))
                .andExpect(jsonPath("$.height", is(12.0)));

        verify(birdService).createBird(any(BirdDTO.class));
    }

    @Test
    void createBird_withInvalidData_shouldReturn400() throws Exception {
        // Arrange - missing required fields
        BirdDTO invalidDTO = new BirdDTO();
        invalidDTO.setName(""); // blank name
        invalidDTO.setWeight(-5.0); // negative weight

        // Act & Assert
        mockMvc.perform(post("/api/birds")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(birdService, never()).createBird(any());
    }

    @Test
    void getBirdById_whenExists_shouldReturn200() throws Exception {
        // Arrange
        BirdDTO birdDTO = new BirdDTO(1L, "Sparrow", "Brown", 10.5, 12.0);
        when(birdService.getBirdById(1L)).thenReturn(birdDTO);

        // Act & Assert
        mockMvc.perform(get("/api/birds/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Sparrow")))
                .andExpect(jsonPath("$.color", is("Brown")));

        verify(birdService).getBirdById(1L);
    }

    @Test
    void getBirdById_whenNotFound_shouldReturn404() throws Exception {
        // Arrange
        when(birdService.getBirdById(999L)).thenThrow(new ResourceNotFoundException("Bird", 999L));

        // Act & Assert
        mockMvc.perform(get("/api/birds/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message").exists());

        verify(birdService).getBirdById(999L);
    }

    @Test
    void getAllBirds_shouldReturnPagedResults() throws Exception {
        // Arrange
        BirdDTO bird1 = new BirdDTO(1L, "Sparrow", "Brown", 10.5, 12.0);
        BirdDTO bird2 = new BirdDTO(2L, "Eagle", "Black", 50.0, 80.0);
        Pageable pageable = PageRequest.of(0, 5);
        Page<BirdDTO> page = new PageImpl<>(Arrays.asList(bird1, bird2), pageable, 2);

        when(birdService.getAllBirds(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/birds")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", is("Sparrow")))
                .andExpect(jsonPath("$.content[1].name", is("Eagle")))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.size", is(5)));

        verify(birdService).getAllBirds(any(Pageable.class));
    }

    @Test
    void updateBird_withValidData_shouldReturn200() throws Exception {
        // Arrange
        BirdDTO updateDTO = new BirdDTO(null, "Updated Sparrow", "Gray", 11.0, 13.0);
        BirdDTO responseDTO = new BirdDTO(1L, "Updated Sparrow", "Gray", 11.0, 13.0);

        when(birdService.updateBird(eq(1L), any(BirdDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/birds/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated Sparrow")))
                .andExpect(jsonPath("$.color", is("Gray")));

        verify(birdService).updateBird(eq(1L), any(BirdDTO.class));
    }

    @Test
    void updateBird_whenNotFound_shouldReturn404() throws Exception {
        // Arrange
        BirdDTO updateDTO = new BirdDTO(null, "Updated Bird", "Blue", 15.0, 20.0);
        when(birdService.updateBird(eq(999L), any(BirdDTO.class)))
                .thenThrow(new ResourceNotFoundException("Bird", 999L));

        // Act & Assert
        mockMvc.perform(put("/api/birds/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(birdService).updateBird(eq(999L), any(BirdDTO.class));
    }

    @Test
    void deleteBird_whenExists_shouldReturn204() throws Exception {
        // Arrange
        doNothing().when(birdService).deleteBird(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/birds/1"))
                .andExpect(status().isNoContent());

        verify(birdService).deleteBird(1L);
    }

    @Test
    void deleteBird_whenNotFound_shouldReturn404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Bird", 999L)).when(birdService).deleteBird(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/birds/999"))
                .andExpect(status().isNotFound());

        verify(birdService).deleteBird(999L);
    }

    @Test
    void searchBirds_withNameAndColor_shouldReturnMatchingBirds() throws Exception {
        // Arrange
        BirdDTO birdDTO = new BirdDTO(1L, "Sparrow", "Brown", 10.5, 12.0);
        Pageable pageable = PageRequest.of(0, 5);
        Page<BirdDTO> page = new PageImpl<>(Collections.singletonList(birdDTO), pageable, 1);

        when(birdService.searchBirdsByNameAndColor(eq("Sparrow"), eq("Brown"), any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/birds/search")
                        .param("name", "Sparrow")
                        .param("color", "Brown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Sparrow")))
                .andExpect(jsonPath("$.content[0].color", is("Brown")));

        verify(birdService).searchBirdsByNameAndColor(eq("Sparrow"), eq("Brown"), any(Pageable.class));
    }

    @Test
    void searchBirds_withNoParameters_shouldReturnAll() throws Exception {
        // Arrange
        BirdDTO birdDTO = new BirdDTO(1L, "Sparrow", "Brown", 10.5, 12.0);
        Pageable pageable = PageRequest.of(0, 5);
        Page<BirdDTO> page = new PageImpl<>(Collections.singletonList(birdDTO), pageable, 1);

        when(birdService.searchBirdsByNameAndColor(isNull(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/birds/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(birdService).searchBirdsByNameAndColor(isNull(), isNull(), any(Pageable.class));
    }
}


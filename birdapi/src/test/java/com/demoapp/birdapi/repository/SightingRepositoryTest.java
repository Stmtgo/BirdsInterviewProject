package com.demoapp.birdapi.repository;

import com.demoapp.birdapi.model.Bird;
import com.demoapp.birdapi.model.Sighting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SightingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SightingRepository sightingRepository;

    private Bird sparrow;
    private Bird eagle;
    private Sighting sighting1;
    private Sighting sighting2;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        sightingRepository.deleteAll();
        entityManager.flush();

        // Create test data
        testDateTime = LocalDateTime.of(2025, 1, 15, 10, 30);

        sparrow = new Bird("Sparrow", "Brown", 10.5, 12.0);
        eagle = new Bird("Eagle", "Black", 50.0, 80.0);

        entityManager.persist(sparrow);
        entityManager.persist(eagle);

        sighting1 = new Sighting(sparrow, "Central Park", testDateTime);
        sighting2 = new Sighting(eagle, "Lake View", testDateTime.plusDays(1));

        entityManager.persist(sighting1);
        entityManager.persist(sighting2);
        entityManager.flush();
    }

    @Test
    void save_shouldPersistSighting() {
        // Arrange
        Sighting newSighting = new Sighting(sparrow, "New Location", testDateTime.plusDays(2));

        // Act
        Sighting saved = sightingRepository.save(newSighting);
        entityManager.flush();

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLocation()).isEqualTo("New Location");
        assertThat(saved.getBird()).isEqualTo(sparrow);

        Sighting found = entityManager.find(Sighting.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getLocation()).isEqualTo("New Location");
    }

    @Test
    void findById_whenExists_shouldReturnSighting() {
        // Act
        Optional<Sighting> found = sightingRepository.findById(sighting1.getId());

        // Assert
        assertTrue(found.isPresent());
        assertThat(found.get().getLocation()).isEqualTo("Central Park");
        assertThat(found.get().getBird().getName()).isEqualTo("Sparrow");
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        // Act
        Optional<Sighting> found = sightingRepository.findById(9999L);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void findAll_shouldReturnAllSightings() {
        // Act
        Iterable<Sighting> sightings = sightingRepository.findAll();

        // Assert
        assertThat(sightings).hasSize(2);
    }

    @Test
    void findAll_withPageable_shouldReturnPagedResults() {
        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<Sighting> page = sightingRepository.findAll(pageable);

        // Assert
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findAll_withSpecification_shouldFilterByBirdId() {
        // Arrange
        Specification<Sighting> spec = (root, query, cb) ->
            cb.equal(root.get("bird").get("id"), sparrow.getId());

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Sighting> result = sightingRepository.findAll(spec, pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBird().getName()).isEqualTo("Sparrow");
    }

    @Test
    void findAll_withSpecification_shouldFilterByLocation() {
        // Arrange
        Specification<Sighting> spec = (root, query, cb) ->
            cb.like(cb.lower(root.get("location")), "%park%");

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Sighting> result = sightingRepository.findAll(spec, pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLocation()).isEqualTo("Central Park");
    }

    @Test
    void findAll_withSpecification_shouldFilterByDateRange() {
        // Arrange
        LocalDateTime fromDate = testDateTime.minusDays(1);
        LocalDateTime toDate = testDateTime.plusHours(12);

        Specification<Sighting> spec = (root, query, cb) ->
            cb.and(
                cb.greaterThanOrEqualTo(root.get("dateTime"), fromDate),
                cb.lessThanOrEqualTo(root.get("dateTime"), toDate)
            );

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Sighting> result = sightingRepository.findAll(spec, pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getLocation()).isEqualTo("Central Park");
    }

    @Test
    void findAll_withSpecification_shouldCombineMultipleFilters() {
        // Arrange
        Specification<Sighting> spec = (root, query, cb) ->
            cb.and(
                cb.equal(root.get("bird").get("id"), sparrow.getId()),
                cb.like(cb.lower(root.get("location")), "%park%")
            );

        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Sighting> result = sightingRepository.findAll(spec, pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBird().getName()).isEqualTo("Sparrow");
        assertThat(result.getContent().get(0).getLocation()).isEqualTo("Central Park");
    }

    @Test
    void update_shouldModifyExistingSighting() {
        // Arrange
        Sighting toUpdate = sightingRepository.findById(sighting1.getId()).orElseThrow();
        toUpdate.setLocation("Updated Park");
        toUpdate.setDateTime(testDateTime.plusHours(5));

        // Act
        Sighting updated = sightingRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Sighting found = sightingRepository.findById(sighting1.getId()).orElseThrow();
        assertThat(found.getLocation()).isEqualTo("Updated Park");
        assertThat(found.getDateTime()).isEqualTo(testDateTime.plusHours(5));
    }

    @Test
    void delete_shouldRemoveSighting() {
        // Arrange
        Long idToDelete = sighting1.getId();

        // Act
        sightingRepository.deleteById(idToDelete);
        entityManager.flush();

        // Assert
        Optional<Sighting> found = sightingRepository.findById(idToDelete);
        assertFalse(found.isPresent());
    }

    @Test
    void existsById_whenExists_shouldReturnTrue() {
        // Act
        boolean exists = sightingRepository.existsById(sighting1.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsById_whenNotExists_shouldReturnFalse() {
        // Act
        boolean exists = sightingRepository.existsById(9999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    void count_shouldReturnTotalSightings() {
        // Act
        long count = sightingRepository.count();

        // Assert
        assertThat(count).isEqualTo(2);
    }

    @Test
    void save_shouldMaintainBirdRelationship() {
        // Arrange
        Sighting newSighting = new Sighting(eagle, "Mountain View", testDateTime.plusDays(3));

        // Act
        Sighting saved = sightingRepository.save(newSighting);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Sighting found = sightingRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getBird()).isNotNull();
        assertThat(found.getBird().getId()).isEqualTo(eagle.getId());
        assertThat(found.getBird().getName()).isEqualTo("Eagle");
    }
}


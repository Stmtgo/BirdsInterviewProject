package com.demoapp.birdapi.repository;

import com.demoapp.birdapi.model.Bird;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BirdRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BirdRepository birdRepository;

    private Bird sparrow;
    private Bird eagle;
    private Bird bluejay;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        birdRepository.deleteAll();
        entityManager.flush();

        // Create test data
        sparrow = new Bird("Sparrow", "Brown", 10.5, 12.0);
        eagle = new Bird("Eagle", "Black", 50.0, 80.0);
        bluejay = new Bird("Blue Jay", "Blue", 15.0, 20.0);

        entityManager.persist(sparrow);
        entityManager.persist(eagle);
        entityManager.persist(bluejay);
        entityManager.flush();
    }

    @Test
    void save_shouldPersistBird() {
        // Arrange
        Bird newBird = new Bird("Robin", "Red", 12.0, 14.0);

        // Act
        Bird saved = birdRepository.save(newBird);
        entityManager.flush();

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Robin");
        assertThat(saved.getColor()).isEqualTo("Red");

        Bird found = entityManager.find(Bird.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Robin");
    }

    @Test
    void findById_whenExists_shouldReturnBird() {
        // Act
        Optional<Bird> found = birdRepository.findById(sparrow.getId());

        // Assert
        assertTrue(found.isPresent());
        assertThat(found.get().getName()).isEqualTo("Sparrow");
        assertThat(found.get().getColor()).isEqualTo("Brown");
    }

    @Test
    void findById_whenNotExists_shouldReturnEmpty() {
        // Act
        Optional<Bird> found = birdRepository.findById(9999L);

        // Assert
        assertFalse(found.isPresent());
    }

    @Test
    void findAll_shouldReturnAllBirds() {
        // Act
        Iterable<Bird> birds = birdRepository.findAll();

        // Assert
        assertThat(birds).hasSize(3);
    }

    @Test
    void findByNameContainingIgnoreCase_shouldReturnMatchingBirds() {
        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bird> result = birdRepository.findByNameContainingIgnoreCase("jay", pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Blue Jay");
    }

    @Test
    void findByNameContainingIgnoreCase_shouldBeCaseInsensitive() {
        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bird> result = birdRepository.findByNameContainingIgnoreCase("SPARROW", pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Sparrow");
    }

    @Test
    void findByColorIgnoreCase_shouldReturnMatchingBirds() {
        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bird> result = birdRepository.findByColorIgnoreCase("brown", pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getColor()).isEqualTo("Brown");
    }

    @Test
    void findByNameContainingIgnoreCaseAndColorIgnoreCase_shouldReturnMatchingBirds() {
        // Arrange
        Bird brownEagle = new Bird("Brown Eagle", "Brown", 45.0, 75.0);
        entityManager.persist(brownEagle);
        entityManager.flush();

        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bird> result = birdRepository.findByNameContainingIgnoreCaseAndColorIgnoreCase("eagle", "brown", pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Brown Eagle");
        assertThat(result.getContent().get(0).getColor()).isEqualTo("Brown");
    }

    @Test
    void findByNameContainingIgnoreCaseAndColorIgnoreCase_withNoMatch_shouldReturnEmpty() {
        // Act
        Pageable pageable = PageRequest.of(0, 10);
        Page<Bird> result = birdRepository.findByNameContainingIgnoreCaseAndColorIgnoreCase("nonexistent", "green", pageable);

        // Assert
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void update_shouldModifyExistingBird() {
        // Arrange
        Bird toUpdate = birdRepository.findById(sparrow.getId()).orElseThrow();
        toUpdate.setName("Updated Sparrow");
        toUpdate.setWeight(11.0);

        // Act
        Bird updated = birdRepository.save(toUpdate);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Bird found = birdRepository.findById(sparrow.getId()).orElseThrow();
        assertThat(found.getName()).isEqualTo("Updated Sparrow");
        assertThat(found.getWeight()).isEqualTo(11.0);
    }

    @Test
    void delete_shouldRemoveBird() {
        // Arrange
        Long idToDelete = sparrow.getId();

        // Act
        birdRepository.deleteById(idToDelete);
        entityManager.flush();

        // Assert
        Optional<Bird> found = birdRepository.findById(idToDelete);
        assertFalse(found.isPresent());
    }

    @Test
    void existsById_whenExists_shouldReturnTrue() {
        // Act
        boolean exists = birdRepository.existsById(sparrow.getId());

        // Assert
        assertTrue(exists);
    }

    @Test
    void existsById_whenNotExists_shouldReturnFalse() {
        // Act
        boolean exists = birdRepository.existsById(9999L);

        // Assert
        assertFalse(exists);
    }

    @Test
    void count_shouldReturnTotalBirds() {
        // Act
        long count = birdRepository.count();

        // Assert
        assertThat(count).isEqualTo(3);
    }
}


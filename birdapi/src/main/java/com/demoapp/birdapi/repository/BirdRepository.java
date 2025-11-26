package com.demoapp.birdapi.repository;

import com.demoapp.birdapi.model.Bird;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirdRepository extends JpaRepository<Bird, Long> {

    Page<Bird> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Bird> findByColorIgnoreCase(String color, Pageable pageable);

    Page<Bird> findByNameContainingIgnoreCaseAndColorIgnoreCase(String name, String color, Pageable pageable);
}


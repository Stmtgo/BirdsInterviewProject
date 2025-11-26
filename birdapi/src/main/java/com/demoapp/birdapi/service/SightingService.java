package com.demoapp.birdapi.service;

import com.demoapp.birdapi.dto.SightingDTO;
import com.demoapp.birdapi.exception.ResourceNotFoundException;
import com.demoapp.birdapi.mapper.SightingMapper;
import com.demoapp.birdapi.model.Bird;
import com.demoapp.birdapi.model.Sighting;
import com.demoapp.birdapi.repository.BirdRepository;
import com.demoapp.birdapi.repository.SightingRepository;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SightingService {

    private static final Logger log = LoggerFactory.getLogger(SightingService.class);

    private final SightingRepository sightingRepository;
    private final BirdRepository birdRepository;
    private final SightingMapper sightingMapper;

    public SightingService(SightingRepository sightingRepository,
                           BirdRepository birdRepository,
                           SightingMapper sightingMapper) {
        this.sightingRepository = sightingRepository;
        this.birdRepository = birdRepository;
        this.sightingMapper = sightingMapper;
    }

    public Page<SightingDTO> getAllSightings(Pageable pageable) {
        log.info("Listing all sightings, pageable={}", pageable);

        return sightingRepository.findAll(pageable)
                .map(sightingMapper::toDTO);
    }

    public SightingDTO getSightingById(Long id) {
        log.info("Retrieving sighting id={}", id);

        Sighting sighting = sightingRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("Sighting not found id={}", id);
                    return new ResourceNotFoundException("Sighting", id);
                });
        return sightingMapper.toDTO(sighting);
    }

    public SightingDTO createSighting(SightingDTO sightingDTO) {
        log.info("Creating sighting payload={}", sightingDTO);

        Bird bird = birdRepository.findById(sightingDTO.getBirdId())
                .orElseThrow(() -> {
                    log.info("Bird not found id={}", sightingDTO.getBirdId());
                    return new ResourceNotFoundException("Bird", sightingDTO.getBirdId());
                });

        Sighting sighting = new Sighting();
        sighting.setBird(bird);
        sighting.setLocation(sightingDTO.getLocation());
        sighting.setDateTime(sightingDTO.getDateTime());

        Sighting savedSighting = sightingRepository.save(sighting);
        log.info("Created sighting id={}", savedSighting.getId());

        return sightingMapper.toDTO(savedSighting);
    }

    public SightingDTO updateSighting(Long id, SightingDTO sightingDTO) {
        log.info("Updating sighting id={} payload={}", id, sightingDTO);
        Sighting sighting = sightingRepository.findById(id)
                .orElseThrow(() -> {
                    log.info("Sighting not found for update id={}", id);
                    return new ResourceNotFoundException("Sighting", id);
                });

        if (sightingDTO.getBirdId() != null) {
            Bird bird = birdRepository.findById(sightingDTO.getBirdId())
                    .orElseThrow(() -> {
                        log.info("Bird not found id={} while updating sighting id={}", sightingDTO.getBirdId(), id);
                        return new ResourceNotFoundException("Bird", sightingDTO.getBirdId());
                    });
            sighting.setBird(bird);
        }

        sighting.setLocation(sightingDTO.getLocation());
        sighting.setDateTime(sightingDTO.getDateTime());

        Sighting updatedSighting = sightingRepository.save(sighting);
        log.info("Updated sighting id={}", updatedSighting.getId());
        return sightingMapper.toDTO(updatedSighting);
    }

    public void deleteSighting(Long id) {
        log.info("Deleting sighting id={}", id);

        if (!sightingRepository.existsById(id)) {
            log.info("Sighting not found for delete id={}", id);
            throw new ResourceNotFoundException("Sighting", id);
        }
        sightingRepository.deleteById(id);

        log.info("Deleted sighting id={}", id);
    }

    public Page<SightingDTO> searchSightings(String birdName, String location, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        log.info("Searching sightings birdId={} location={} fromDate={} toDate={} pageable={}", birdName, location, fromDate, toDate, pageable);

        Specification<Sighting> spec = (Root<Sighting> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (birdName != null && !birdName.isEmpty()) {
                predicates.add(cb.equal(root.get("bird").get("name"), birdName));
            }
            if (location != null && !location.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("location").as(String.class)), "%" + location.toLowerCase() + "%"));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime").as(LocalDateTime.class), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTime").as(LocalDateTime.class), toDate));
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<SightingDTO> result = sightingRepository.findAll(spec, pageable).map(sightingMapper::toDTO);
        log.info("Search returned {} results (page size {})", result.getTotalElements(), pageable.getPageSize());

        return result;
    }


}


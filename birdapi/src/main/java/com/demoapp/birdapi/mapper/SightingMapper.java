package com.demoapp.birdapi.mapper;

import com.demoapp.birdapi.dto.SightingDTO;
import com.demoapp.birdapi.model.Sighting;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {BirdMapper.class})
public interface SightingMapper {

    @Mapping(source = "bird.id", target = "birdId")
    @Mapping(source = "bird", target = "bird")
    SightingDTO toDTO(Sighting sighting);

    @Mapping(source = "birdId", target = "bird.id")
    Sighting toEntity(SightingDTO sightingDTO);

    @Mapping(source = "birdId", target = "bird.id")
    void updateEntityFromDTO(SightingDTO sightingDTO, @MappingTarget Sighting sighting);
}


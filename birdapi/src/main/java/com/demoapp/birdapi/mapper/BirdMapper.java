package com.demoapp.birdapi.mapper;

import com.demoapp.birdapi.dto.BirdDTO;
import com.demoapp.birdapi.model.Bird;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BirdMapper {

    BirdDTO toDTO(Bird bird);

    Bird toEntity(BirdDTO birdDTO);

    void updateEntityFromDTO(BirdDTO birdDTO, @MappingTarget Bird bird);
}


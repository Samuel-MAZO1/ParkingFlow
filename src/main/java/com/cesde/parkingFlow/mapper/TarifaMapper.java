package com.cesde.parkingFlow.mapper;

import com.cesde.parkingFlow.dto.TarifaRequestDTO;
import com.cesde.parkingFlow.entity.Tarifa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.cesde.parkingFlow.dto.response.TarifaResponseDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TarifaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", constant = "true")
    Tarifa toEntity(TarifaRequestDTO dto);

    
    TarifaResponseDTO toResponseDTO(Tarifa tarifa);

    // Convierte listas completas
    List<TarifaResponseDTO> toResponseDTOList(List<Tarifa> tarifas);

    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", ignore = true)
    void updateEntityFromDto(TarifaRequestDTO dto, @MappingTarget Tarifa entity);
}
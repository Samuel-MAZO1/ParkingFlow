package com.cesde.parkingFlow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cesde.parkingFlow.dto.TarifaRequestDTO;
import com.cesde.parkingFlow.dto.response.TarifaResponseDTO;
import com.cesde.parkingFlow.entity.Tarifa;
import com.cesde.parkingFlow.enums.TipoVehiculo;
import com.cesde.parkingFlow.exception.custom.NotFound;
import com.cesde.parkingFlow.mapper.TarifaMapper;
import com.cesde.parkingFlow.repository.TarifaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarifaService {

    private final TarifaRepository tarifaRepository;
    private final TarifaMapper tarifaMapper; 

    @Transactional
    public TarifaResponseDTO crearTarifa(TarifaRequestDTO dto) {
       
        Tarifa nuevaTarifa = tarifaMapper.toEntity(dto);
        
        Tarifa guardada = tarifaRepository.save(nuevaTarifa);
        
        return tarifaMapper.toResponseDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<TarifaResponseDTO> listarActivas() {
        List<Tarifa> activas = tarifaRepository.findByActivoTrue();
        return tarifaMapper.toResponseDTOList(activas);
    }
    
    @Transactional(readOnly = true)
    public TarifaResponseDTO obtenerPorId(Long id) {
        Tarifa tarifa = findTarifaById(id);
        
        return tarifaMapper.toResponseDTO(tarifa);
    }
    
    @Transactional
    public TarifaResponseDTO actualizarTarifa(Long id, TarifaRequestDTO dto) {
        Tarifa tarifaExistente = findTarifaById(id);

        // MapStruct actualiza los campos de 'tarifaExistente' con los datos de 'dto'
        tarifaMapper.updateEntityFromDto(dto, tarifaExistente);

        Tarifa actualizada = tarifaRepository.save(tarifaExistente);
        return tarifaMapper.toResponseDTO(actualizada);
}
    
    @Transactional
    public void desactivarTarifa(Long id) {
        Tarifa tarifa = findTarifaById(id);
        
        tarifa.setActivo(false);
        tarifaRepository.save(tarifa);
    }
    
    @Transactional(readOnly = true)
    public List<TarifaResponseDTO> buscarTarifasPorTipo(TipoVehiculo tipo) {
        List<Tarifa> tarifas = tarifaRepository.findByTipoVehiculoAndActivoTrue(tipo);
        return tarifaMapper.toResponseDTOList(tarifas);
    }
    
    public Tarifa findTarifaById(Long id) {
    	Tarifa tarifa = tarifaRepository.findById(id)
                .orElseThrow(() -> new NotFound("No se puede desactivar: Tarifa no existe."));
        
    	return tarifa;
    }
    
}
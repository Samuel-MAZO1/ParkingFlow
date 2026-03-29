package com.cesde.parkingFlow.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.cesde.parkingFlow.dto.CapacidadTipoDTO;
import com.cesde.parkingFlow.dto.EstadoParqueaderoDTO;
import com.cesde.parkingFlow.entity.ConfiguracionCapacidad;
import com.cesde.parkingFlow.entity.Parqueadero;
import com.cesde.parkingFlow.enums.TipoVehiculo;
import com.cesde.parkingFlow.exception.custom.RegistroInvalido;
import com.cesde.parkingFlow.repository.ConfiguracionCapacidadRepository;
import com.cesde.parkingFlow.repository.ParqueaderoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CapacidadService {

    private final ParqueaderoRepository parqueaderoRepository;
    private final ConfiguracionCapacidadRepository capacidadRepository;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void configurarCapacidad(TipoVehiculo tipo, Integer nuevaCapacidad) {
       
    	Parqueadero sede = obtenerSedePrincipal();

        // Buscar si ya existe la configuración para ese tipo
        ConfiguracionCapacidad config = capacidadRepository.findByTipoVehiculo(tipo)
                .orElse(new ConfiguracionCapacidad());

        // No puede ser menor a los vehículos actualmente estacionados
        if (config.getOcupacionActual() != null && nuevaCapacidad < config.getOcupacionActual()) {
            throw new RegistroInvalido("No se puede reducir la capacidad a " + nuevaCapacidad + 
                " porque hay " + config.getOcupacionActual() + " vehículos de tipo " + tipo + " adentro.");
        }

        //  No exceder la capacidad física total del parqueadero
        Integer asignadoOtros = capacidadRepository.sumarAsignadoOtrosTipos(tipo);
        if (asignadoOtros == null) asignadoOtros = 0;

        if ((asignadoOtros + nuevaCapacidad) > sede.getCapacidadFisicaTotal()) {
            throw new RegistroInvalido("La suma de capacidades excede el límite físico de " + 
                sede.getCapacidadFisicaTotal() + " celdas.");
        }

       
        config.setParqueadero(sede);
        config.setTipoVehiculo(tipo);
        config.setCapacidadAsignada(nuevaCapacidad);
        capacidadRepository.save(config);
    }
    
 

    public EstadoParqueaderoDTO obtenerEstadoCompleto() {
 
    	Parqueadero sede = obtenerSedePrincipal();
        
        List<ConfiguracionCapacidad> configuraciones = capacidadRepository.findAll();

        // Mapear los detalles por tipo
        List<CapacidadTipoDTO> detalles = configuraciones.stream().map(c -> {
            return new CapacidadTipoDTO(
                c.getTipoVehiculo(),
                c.getCapacidadAsignada(),
                c.getOcupacionActual(),
                c.getCapacidadAsignada() - c.getOcupacionActual(),
                c.getOcupacionActual() // El mínimo permitido es lo que ya está adentro
            );
        }).collect(Collectors.toList());

        // Calcular capacidad por asignar (lo que sobra de los 60 cupos)
        Integer totalAsignado = detalles.stream()
                .mapToInt(CapacidadTipoDTO::getCapacidadAsignada).sum();

        // Construir el tablero de control
        return EstadoParqueaderoDTO.builder()
                .nombreParqueadero(sede.getNombre())
                .capacidadFisicaTotal(sede.getCapacidadFisicaTotal())
                .ocupacionGlobal(sede.getOcupacionGlobal())
                .cuposFisicosLibres(sede.getCapacidadFisicaTotal() - sede.getOcupacionGlobal())
                .detallePorTipo(detalles)
                .capacidadPorAsignar(sede.getCapacidadFisicaTotal() - totalAsignado)
                .build();
    }
    
    private Parqueadero obtenerSedePrincipal() {
        return parqueaderoRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Error: El parqueadero no ha sido inicializado en la base de datos."));
    }
}
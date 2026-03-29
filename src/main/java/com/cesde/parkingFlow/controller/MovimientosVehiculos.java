package com.cesde.parkingFlow.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cesde.parkingFlow.entity.ConfiguracionCapacidad;
import com.cesde.parkingFlow.entity.Parqueadero;
import com.cesde.parkingFlow.enums.TipoVehiculo;
import com.cesde.parkingFlow.repository.ConfiguracionCapacidadRepository;
import com.cesde.parkingFlow.repository.ParqueaderoRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/movimientos")
@RequiredArgsConstructor
public class MovimientosVehiculos {

    private final ParqueaderoRepository parqueaderoRepository;
    private final ConfiguracionCapacidadRepository capacidadRepository;

    @PostMapping("/entrada/{tipo}")
    @Transactional
    public ResponseEntity<String> simularEntrada(@PathVariable TipoVehiculo tipo) {
        // 1. Buscamos la config de ese tipo
        ConfiguracionCapacidad config = capacidadRepository.findByTipoVehiculo(tipo)
                .orElseThrow(() -> new RuntimeException("Configura primero la capacidad para " + tipo));

        // 2. Validamos si hay espacio (Lógica que usaremos en la historia de Ingresos)
        if (config.getOcupacionActual() >= config.getCapacidadAsignada()) {
            return ResponseEntity.badRequest().body("¡No hay cupo para " + tipo + "!");
        }

        // 3. Incrementamos contadores
        config.setOcupacionActual(config.getOcupacionActual() + 1);
        
        Parqueadero sede = config.getParqueadero();
        sede.setOcupacionGlobal(sede.getOcupacionGlobal() + 1);

        capacidadRepository.save(config);
        parqueaderoRepository.save(sede);

        return ResponseEntity.ok("Entrada registrada. Ocupación de " + tipo + ": " + config.getOcupacionActual());
    }

    @PostMapping("/salida/{tipo}")
    @Transactional
    public ResponseEntity<String> simularSalida(@PathVariable TipoVehiculo tipo) {
        ConfiguracionCapacidad config = capacidadRepository.findByTipoVehiculo(tipo)
                .orElseThrow(() -> new RuntimeException("No hay configuración"));

        if (config.getOcupacionActual() > 0) {
            config.setOcupacionActual(config.getOcupacionActual() - 1);
            
            Parqueadero sede = config.getParqueadero();
            sede.setOcupacionGlobal(sede.getOcupacionGlobal() - 1);

            capacidadRepository.save(config);
            parqueaderoRepository.save(sede);
        }

        return ResponseEntity.ok("Salida registrada. Ocupación de " + tipo + ": " + config.getOcupacionActual());
    }
}
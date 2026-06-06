package com.cesde.parkingFlow.repository;

import com.cesde.parkingFlow.entity.RegistroEstacionamiento;
import com.cesde.parkingFlow.enums.EstadoRegistro;
import com.cesde.parkingFlow.enums.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RegistroEstacionamientoRepository extends JpaRepository<RegistroEstacionamiento, Long> {
    
    // Cuenta los espacios ocupados actualmente por tipo de vehículo
    long countByTipoVehiculoAndEstado(TipoVehiculo tipoVehiculo, EstadoRegistro estado);

    // Valida que el vehículo no intente registrar una entrada duplicada sin haber salido
    boolean existsByPlacaAndEstado(String placa, EstadoRegistro estado);

    Optional<RegistroEstacionamiento> findByIdAndEstado(Long id, EstadoRegistro estado);
    Optional<RegistroEstacionamiento> findByPlacaAndEstado(String placa, EstadoRegistro estado);
}
package com.cesde.parkingFlow.repository;

import com.cesde.parkingFlow.entity.Suscripcion;
import com.cesde.parkingFlow.entity.Vehiculo;
import com.cesde.parkingFlow.enums.EstadoSuscripcion;
import com.cesde.parkingFlow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {
    
    boolean existsByVehiculoAndEstado(Vehiculo vehiculo, EstadoSuscripcion estado);

    // Consulta para la US-011: Obtiene suscripciones activas o vencidas hace máximo un mes
    @Query("SELECT s FROM Suscripcion s " +
           "WHERE s.vehiculo.user = :user " +
           "AND (s.estado = com.cesde.parkingFlow.enums.EstadoSuscripcion.ACTIVA " +
           "OR (s.estado = com.cesde.parkingFlow.enums.EstadoSuscripcion.VENCIDA AND s.fechaFin >= :limiteFecha)) " +
           "ORDER BY s.fechaFin DESC")
    List<Suscripcion> findActivasYVencidasRecientes(
            @Param("user") User user, 
            @Param("limiteFecha") LocalDate limiteFecha
    );
}
package com.cesde.parkingFlow.repository;

import com.cesde.parkingFlow.entity.PlanAbonado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlanAbonadoRepository extends JpaRepository<PlanAbonado, Long> {
    // Permite filtrar planes vigentes para los abonados expuestos en el frontend
    List<PlanAbonado> findByActivoTrue();
}
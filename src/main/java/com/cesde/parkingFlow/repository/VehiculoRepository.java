package com.cesde.parkingFlow.repository;

import com.cesde.parkingFlow.entity.Vehiculo;
import com.cesde.parkingFlow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    boolean existsByPlaca(String placa);
    long countByUser(User user);
}
